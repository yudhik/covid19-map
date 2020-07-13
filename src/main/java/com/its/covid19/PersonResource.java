package com.its.covid19;

import com.its.covid19.util.CommonUtil;
import java.net.URI;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.CompletionStage;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Query;
import org.neo4j.driver.Value;
import org.neo4j.driver.Values;
import org.neo4j.driver.async.AsyncSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.its.covid19.model.Condition;
import com.its.covid19.model.Location;
import com.its.covid19.model.Person;
import com.its.covid19.vo.ConditionLocation;

@Path("/persons")
public class PersonResource {

  private static final Logger LOGGER = LoggerFactory.getLogger(PersonResource.class);
  private static final ObjectMapper MAPPER = new ObjectMapper();

  @Inject
  Driver driver;

  @GET
  public CompletionStage<Response> getAll() {
    AsyncSession session = driver.asyncSession();
    return session.runAsync("MATCH (person:Person) RETURN person ORDER BY person.firstName")
        .thenCompose(
            cursor -> cursor.listAsync(record -> Person.from(record.get("person").asNode())))
        .thenCompose(person -> session.closeAsync().thenApply(signal -> person))
        .thenApply(Response::ok).thenApply(ResponseBuilder::build);
  }

  @GET
  @Path("/{id}/conditions")
  @Produces(MediaType.APPLICATION_JSON)
  public CompletionStage<Response> getConditions(@PathParam("id") String id) {
    LOGGER.info("get conditions for {}", id);
    AsyncSession session = driver.asyncSession();
    Query query = new Query(
        "MATCH (:Person{id:$id})<-[:HEALTH]-(condition) -[l:LOCATED]-> (location) RETURN condition,l,location",
        Values.parameters("id", id));
    return session.runAsync(query).thenCompose(cursor -> cursor.listAsync(record -> {
      Condition condition = Condition.from(record.get("condition").asNode());
      Location location = Location.from(record.get("location").asNode());
      return new ConditionLocation(condition.getId(), condition.getTemperature(),
          condition.isCough(), condition.isFever(), condition.isShortnessOfBreath(),
          record.get("l").asNode().get("createdDate").asLocalDateTime(), location);
    })).thenCompose(condition -> session.closeAsync().thenApply(signal -> condition))
        .thenApply(Response::ok).thenApply(ResponseBuilder::build);
  }

  @GET
  @Path("/{id}")
  public CompletionStage<Response> getPerson(@PathParam("id") String id) {
    AsyncSession session = driver.asyncSession();
    return session
        .runAsync("MATCH (person:Person{id: $id}) RETURN person", Values.parameters("id", id))
        .thenCompose(cursor -> cursor.singleAsync())
        .thenApply(record -> Person.from(record.get("person").asNode()))
        .thenCompose(person -> session.closeAsync().thenApply(signal -> person))
        .thenApply(person -> {
          try {
            return Response.ok().entity(MAPPER.writeValueAsString(person)).build();
          } catch (JsonProcessingException e) {
            LOGGER.error("can not write json", e);
            return Response.serverError().build();
          }
        });
  }

  @POST
  @Path("/{id}/conditions")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public CompletionStage<Response> postCondition(@PathParam("id") String id,
      ConditionLocation conditionLocation) {
    LOGGER.info("{} post {}", id, conditionLocation.toString());
    AsyncSession session = driver.asyncSession();
    String conditionId = CommonUtil.generateRandomUUID();
    String locationId = CommonUtil.generateRandomUUID();
    conditionLocation.setId(conditionId);
    conditionLocation.getLocation().setId(locationId);
    LOGGER.info("saving {}", conditionLocation);

    Value createConditionLocationValue = Values.parameters("conditionId", conditionLocation.getId(),
        "temperature", conditionLocation.getTemperature(), "cough", conditionLocation.isCough(),
        "fever", conditionLocation.isFever(), "shortnessOfBreath",
        conditionLocation.isShortnessOfBreath(), "locationId", locationId, "address",
        conditionLocation.getLocation().getAddress(), "longitude",
        conditionLocation.getLocation().getLongitude(), "latitude",
        conditionLocation.getLocation().getLatitude(), "altitude",
        conditionLocation.getLocation().getAltitude());

    Value personConditionValue = Values.parameters("personId", id, "conditionId", conditionId);
    Value conditionLocationValue =
        Values.parameters("conditionId", conditionId, "locationId", locationId);
    return session.writeTransactionAsync(transaction -> {
      transaction.runAsync(Condition.CREATE_CONDITION_LOCATION_STATEMENT,
          createConditionLocationValue);
      // transaction.runAsync(Location.CREATE_STATEMENT, createLocationValue);
      transaction.runAsync(Location.RELATE_CONDITION_LOCATION, conditionLocationValue);
      return transaction.runAsync(Condition.RELATE_PERSON_HEALTH, personConditionValue)
          .thenCompose(fn -> fn.singleAsync());
    }).thenApply(record -> Condition.from(record.get("condition").asNode()))
        .thenCompose(
            persistedCondition -> session.closeAsync().thenApply(signal -> persistedCondition))
        .thenApply(persistedCondition -> Response.ok().entity(persistedCondition)
            .contentLocation(URI
                .create(String.format("/persons/%s/conditions/%s", id, persistedCondition.getId())))
            .build());
  }

  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public CompletionStage<Response> registerMember(Person person) {
    LOGGER.info(person.toString());
    AsyncSession session = driver.asyncSession();
    person.setId(UUID.randomUUID().toString().replace("-", "").toUpperCase(Locale.ENGLISH));
    @SuppressWarnings("deprecation")
    Value createValue = Values.parameters("id", person.getId(), "firstName", person.getFirstName(),
        "middleName", person.getMiddleName(), "lastName", person.getLastName(), "birthYear",
        1900 + person.getDateOfBirth().getYear(), "birthMonth", person.getDateOfBirth().getMonth(),
        "birthDate", person.getDateOfBirth().getDate(), "gender", person.getGender().toString(),
        "accountId", person.getAccountId());
    return session
        .writeTransactionAsync(transaction -> transaction
            .runAsync(Person.CREATE_STATEMENT, createValue).thenCompose(fn -> fn.singleAsync()))
        .thenApply(record -> Person.from(record.get("person").asNode()))
        .thenCompose(persistedPerson -> session.closeAsync().thenApply(signal -> persistedPerson))
        .thenApply(persistedPerson -> Response.ok().entity(persistedPerson)
            .contentLocation(URI.create("/persons/" + persistedPerson.getId())).build());
  }
}

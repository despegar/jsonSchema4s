package com.despegar.library.serialization.json

import org.scalatest.{FunSpecLike, MustMatchers}

class JsonSchemaGeneratorTest extends FunSpecLike with MustMatchers {
  import JsonSchemaGeneratorTest._
  
  describe("Generating schema for primitives type") {
    it("must work with String type") {
      JsonSchemaGenerator.generateSchema[String] mustEqual JsonSchemaPrimitive(JsonSchemaType.String)
    }
    
    it("must work with Int type") {
      JsonSchemaGenerator.generateSchema[Int] mustEqual JsonSchemaPrimitive(JsonSchemaType.Integer)
    }
    
    it("must work with BigDecimal type") {
      JsonSchemaGenerator.generateSchema[BigDecimal] mustEqual JsonSchemaPrimitive(JsonSchemaType.Number)
    }
    
    it("must work with Boolean type") {
      JsonSchemaGenerator.generateSchema[Boolean] mustEqual JsonSchemaPrimitive(JsonSchemaType.Boolean)
    }
  }
  
  describe("Generating schema for complex types") {
    it("must work with a case class of all") {
      val actual = JsonSchemaGenerator.generateSchema[Person]

      val job = JsonSchemaObject("job", Map("company" -> JsonSchemaPrimitive(JsonSchemaType.String),
        "description" -> JsonSchemaPrimitive(JsonSchemaType.String)), Seq("company", "description"))

      val personProperties = Map(
        "job" -> job,
        "phones" -> JsonSchemaArray(JsonSchemaPrimitive(JsonSchemaType.Integer)),
        "name" -> JsonSchemaPrimitive(JsonSchemaType.String),
        "previousJobs" -> JsonSchemaArray(job))
      val expected = JsonSchemaObject("person", personProperties, Seq("phones", "name"))
      actual mustEqual expected
    }
    
    it("must work with a parametrized case class") {
      val actual = JsonSchemaGenerator.generateSchema[ParametrizedClass[Int, String]]
      val expected = JsonSchemaObject("parametrizedclass", Map("other" -> JsonSchemaArray(JsonSchemaPrimitive(JsonSchemaType.String)),
          "some" -> JsonSchemaPrimitive(JsonSchemaType.Integer)), Seq("other", "some"))
      actual mustEqual expected
    }


    it("must work with recursive classes") {
      val actual = JsonSchemaGenerator.generateSchema[PersonRecursive]
      val personProperties = Map("job" -> JsonSchemaObject("job", Map("company" -> JsonSchemaPrimitive(JsonSchemaType.String),
        "description" -> JsonSchemaPrimitive(JsonSchemaType.String)), Seq("company", "description")),
        "phones" -> JsonSchemaArray(JsonSchemaPrimitive(JsonSchemaType.Integer)),
        "name" -> JsonSchemaPrimitive(JsonSchemaType.String),
        "parent" -> JsonSchemaRefObject("personrecursive"))
      val expected = JsonSchemaObject("personrecursive", personProperties, Seq("phones", "name"))
      actual mustEqual expected
    }
  }
}

object JsonSchemaGeneratorTest {
  case class Person(name: String, phones: Seq[Int], job: Option[Job], previousJobs: Option[Seq[Job]])
  case class PersonRecursive(name: String, phones: Seq[Int], job: Option[Job], parent: Option[PersonRecursive])
  case class Job(description: String, company: String)
  case class ParametrizedClass[A, B](some: A, other: Seq[B])
}
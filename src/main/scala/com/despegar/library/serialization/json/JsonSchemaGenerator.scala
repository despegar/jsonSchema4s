package com.despegar.library.serialization.json

import scala.reflect.runtime.universe._

/**
 * A class that generates json schema from a type.
 */
trait JsonSchemaGenerator {
  /**
   * Generates the json schema from the type 'T'.
   * Don't user recursive types! (for now).
   */
  def generateSchema[T: TypeTag]: JsonSchemaElement = generateSchemaFromType(typeOf[T])

  /**
   * Generates a json schema from a Type.
   */
  private def generateSchemaFromType(tpe: Type): JsonSchemaElement = tpe match {
    case _ if (tpe <:< typeOf[Option[_]]) =>
      val realTpe = tpe match { case TypeRef(_, _, Seq(realTpe)) => realTpe }
      JsonSchemaOption(generateSchemaFromType(realTpe))
    case _ if (tpe <:< typeOf[Seq[_]]) =>
      val realTpe = tpe match { case TypeRef(_, _, Seq(realTpe)) => realTpe }
      JsonSchemaArray(generateSchemaFromType(realTpe))
    case _ if (tpe <:< typeOf[Product]) =>
      val fields = tpe.members.collect { case m: MethodSymbol if m.isGetter => m }.toSeq
      val schemaFields = fields.map(f => (f.name.toString, generateSchemaFromType(f.returnType)))
      val required = schemaFields.collect { case (name, schema) if !schema.isInstanceOf[JsonSchemaOption] => name }
      val finalSchemaFields = schemaFields.map {
        case (name, JsonSchemaOption(real)) => (name, real)
        case other => other
      }
      JsonSchemaObject(finalSchemaFields.toMap, required)
    /* Primitives */
    case _ if (tpe <:< typeOf[String]) => JsonSchemaPrimitive(JsonSchemaType.String)
    case _ if (tpe <:< typeOf[Int]) => JsonSchemaPrimitive(JsonSchemaType.Integer)
    case _ if (tpe <:< typeOf[BigDecimal]) => JsonSchemaPrimitive(JsonSchemaType.Number)
    case _ if (tpe <:< typeOf[Boolean]) => JsonSchemaPrimitive(JsonSchemaType.Boolean)
  }
}

object JsonSchemaGenerator extends JsonSchemaGenerator
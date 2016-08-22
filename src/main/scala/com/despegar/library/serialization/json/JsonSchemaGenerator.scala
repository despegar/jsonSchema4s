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
  private def generateSchemaFromType(tpe: Type, hints: Map[String, Type] = Map(),
                                     preprocessedIds: Seq[String] = Nil): JsonSchemaElement = tpe match {
    case _ if (tpe <:< typeOf[Option[_]]) =>
      val realTpe = tpe match { case TypeRef(_, _, Seq(realTpe)) => realTpe }
      JsonSchemaOption(generateSchemaFromType(realTpe, hints, preprocessedIds))
    case _ if (tpe <:< typeOf[Seq[_]]) =>
      val realTpe = tpe match { case TypeRef(_, _, Seq(realTpe)) => realTpe }
      JsonSchemaArray(generateSchemaFromType(realTpe, hints, preprocessedIds))
    case _ if (tpe <:< typeOf[Product]) =>
      val typeId = generateId(tpe)
      if (preprocessedIds.contains(typeId)) JsonSchemaRefObject(typeId) else {
        val typeParametersMap = tpe match {
          case TypeRef(a, b, c) => b.asClass.typeParams.map(_.name.toString).zip(c).toMap
        }
        val fields = tpe.members.collect { case m: MethodSymbol if m.isGetter => m }.toSeq
        val schemaFields = fields.map(f => (f.name.toString, generateSchemaFromType(f.returnType,
          hints ++ typeParametersMap, preprocessedIds :+ typeId)))
        val required = schemaFields.collect { case (name, schema) if !schema.isInstanceOf[JsonSchemaOption] => name }
        val finalSchemaFields = schemaFields.map {
          case (name, JsonSchemaOption(real)) => (name, real)
          case other => other
        }
        JsonSchemaObject(typeId, finalSchemaFields.toMap, required)
      }
    /* Primitives */
    case _ if (tpe <:< typeOf[String]) => JsonSchemaPrimitive(JsonSchemaType.String)
    case _ if (tpe <:< typeOf[Int] || tpe <:< typeOf[Long])  => JsonSchemaPrimitive(JsonSchemaType.Integer)
    case _ if (tpe <:< typeOf[BigDecimal]) => JsonSchemaPrimitive(JsonSchemaType.Number)
    case _ if (tpe <:< typeOf[Boolean]) => JsonSchemaPrimitive(JsonSchemaType.Boolean)
    case _ if hints.contains(tpe.toString) => generateSchemaFromType(hints(tpe.toString))
  }

  /**
   * Generates an id from a Type.
   */
  private def generateId(tpe: Type): String =
    tpe.toString.split("\\.").last.toLowerCase.split("\\[").head
}

object JsonSchemaGenerator extends JsonSchemaGenerator
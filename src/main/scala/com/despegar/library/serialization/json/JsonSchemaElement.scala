package com.despegar.library.serialization.json

/**
 * The base json schema element.
 */
trait JsonSchemaElement {
  val `type`: String
}

/**
 * The json schema element used for primitive types.
 * 
 * @param type The primitive type of the element.
 */
case class JsonSchemaPrimitive(`type`: String) extends JsonSchemaElement

/**
 * The json schema element used for the object type.
 *
 * @param id The object schema identifier.
 * @param properties A map with the property name as key and the element as value.
 * @param required The sequence of mandatory properties.
 * @param type The type 'object'. Don't modify, it's only for serialization purpose.
 */
case class JsonSchemaObject(id: String, properties: Map[String, JsonSchemaElement], required: Seq[String],
                            `type`: String = JsonSchemaType.Object) extends JsonSchemaElement

/**
  * The json schema element used for the object type.
  *
  * @param $ref The reference schema identifier.
  * @param type The type 'object'. Don't modify, it's only for serialization purpose.
  */
case class JsonSchemaRefObject($ref: String, `type`: String = JsonSchemaType.Object) extends JsonSchemaElement

/**
 * A helper json schema element used for parsing Options.
 * 
 * @param real The optional element.
 */
private case class JsonSchemaOption(real: JsonSchemaElement) extends JsonSchemaElement {
  val `type` = "none"
}

/**
 * The json schema element used for the array type.
 * 
 * @param items The definition of the items of the array.
 * @param type The type 'array'. Don't modify, it's only for serialization purpose.
 */
case class JsonSchemaArray(items: JsonSchemaElement, `type`: String = JsonSchemaType.Array) extends JsonSchemaElement
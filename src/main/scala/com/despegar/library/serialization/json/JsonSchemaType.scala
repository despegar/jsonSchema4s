package com.despegar.library.serialization.json

/**
 * The primitive types for JSON values.
 */
object JsonSchemaType {
  /**
   * A JSON string.
   */
  val String = "string"
    
  /**
   * A JSON number without a fraction or exponent part.
   */
  val Integer = "integer"
    
  /**
   * Any JSON number. Number includes integer.
   */
  val Number = "number"
    
  /**
   * A JSON boolean.
   */
  val Boolean = "boolean"
    
  /**
   * A JSON object.
   */
  val Object = "object"
    
  /**
   * A JSON array.
   */
  val Array = "array"
}
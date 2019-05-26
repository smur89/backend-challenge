package model

import play.api.libs.json.{Format, Json}

case class MessageResponse(status: Int, message: String) extends ApiResponse

object MessageResponse {
  implicit val messageResponseFormat: Format[MessageResponse] = Json.format[MessageResponse]
}

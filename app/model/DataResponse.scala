package model

import play.api.libs.json.{Format, Json}

case class DataResponse[T](status: Int, data: T) extends ApiResponse

object DataResponse {
  implicit def dataResponseFormat[T](implicit format: Format[T]): Format[DataResponse[T]] = Json.format[DataResponse[T]]
}

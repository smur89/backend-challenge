package model.exceptions

case class DuplicateIdException(message: String = "Not created. The Id is already in use.") extends Exception

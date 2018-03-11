package com.datametica.cockatoos.utils

import java.io.{File, FileNotFoundException}

import com.fasterxml.jackson.databind.{DeserializationFeature, ObjectMapper}
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.datametica.cockatoos.exceptions.CockatoosException
import org.apache.commons.io.FilenameUtils
import org.json4s.DefaultFormats
import org.json4s.native.JsonMethods

object FileUtils {
  def getListOfFiles(dir: String): List[File] = {
    val d = new File(dir)
    if (d.isDirectory) {
      d.listFiles.filter(_.isFile).toList
    } else if (d.isFile) {
      List(d)
    } else {
      throw new FileNotFoundException(s"No Files to Run ${dir}")
    }
  }

  def jsonFileToObject[T: Manifest](file: File): T = {
    implicit val formats = DefaultFormats
    val jsonString = scala.io.Source.fromFile(file).mkString

    try {
      val json = JsonMethods.parse(jsonString)
      json.extract[T]
    } catch {
      case cast: ClassCastException => throw CockatoosException(s"Failed to cast json file " + file, cast)
      case other: Throwable => throw other
    }
  }

  def getContentFromFileAsString(file: File): String = {
    scala.io.Source.fromFile(file).mkString //    //By scala.io. on read spark fail with legit error when path does not exists
  }

  def getObjectMapperByExtension(fileName: String): Option[ObjectMapper] = {
    val extension = FilenameUtils.getExtension(fileName)
    extension match {
      case "json" => Option(new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false))
      case "yaml" | "yml" | _ => Option(new ObjectMapper(new YAMLFactory()).configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false))
    }
  }
}

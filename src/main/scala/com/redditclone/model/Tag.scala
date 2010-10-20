package com.redditclone.model

import net.liftweb._
import mapper._
import util._
import Helpers._

object Tag extends Tag with LongKeyedMetaMapper[Tag] {
  def byName (reditLinkId : Long, name : String) = 
    findAll(By(Tag.reditLink, reditLinkId), By(Tag.name, name)) match {
      case tag :: rest => tag
      // create a tag for the given name if it doesn't exist... 
      case Nil => Tag.create.name(name).reditLink(reditLinkId).saveMe
    }
}

class Tag extends LongKeyedMapper[Tag] with IdPK {
  def getSingleton = Tag
  
  // Just like MappedString, except it's defaultValue is "" and the length is auto-cropped to fit in the column
  object name extends MappedPoliteString(this, 64) {
    override def setFilter = notNull _ :: trim _ :: super.setFilter 
  }

  // Each tag belongs to a specific reditLink.
  object reditLink extends MappedLongForeignKey(this, ReditLink) {
    override def dbIndexed_? = true
  }
}

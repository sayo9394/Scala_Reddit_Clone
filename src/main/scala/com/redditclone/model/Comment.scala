package com.redditclone.model

import net.liftweb._
import mapper._
import util._
import Helpers._

class Comment extends LongKeyedMapper[Comment] with IdPK {
  def getSingleton = Comment
  
  // Just like MappedString, except it's defaultValue is "" and the length is auto-cropped to fit in the column
  object title extends MappedString(this, 100) 
  object description extends MappedString(this,300)		// actual comment
  
  // Each comment belongs to a specific owner.
  object owner extends MappedLongForeignKey(this, User) {
    override def dbIndexed_? = true
  }
  // Each tag belongs to a specific reditLink.
  object reditLink extends MappedLongForeignKey(this, ReditLink) {
    override def dbIndexed_? = true
  }
}


object Comment extends Comment with LongKeyedMetaMapper[Comment] {
  def byUser (owner : User) : List[Comment] = 
    Comment.findAll(By(Comment.owner, owner.id.is))
    
  def byLink (lnk : ReditLink) : List[Comment] = 
    Comment.findAll(By(Comment.reditLink, lnk.id.is))
}

package com.redditclone.model

import net.liftweb.mapper._
import net.liftweb.util._
import scala.xml.{NodeSeq,Text}
import java.text.{DateFormat,SimpleDateFormat}
import java.util.Date

class ReditLink extends LongKeyedMapper[ReditLink] with IdPK {
  def getSingleton = ReditLink

  def ranks : List[Rank] = Rank.findAll(By(Rank.lnk, this.id))
  
  object owner extends MappedLongForeignKey(this, User) {
    override def dbIndexed_? = true
  }

  object is_public extends MappedBoolean(this) {
    override def defaultValue = false
  }

  object dateOf extends MappedDateTime(this) {
    final val dateFormat = 
      DateFormat.getDateInstance(DateFormat.SHORT)
    override def asHtml = Text(dateFormat.format(is))
  }
  
  object rank extends MappedInt(this)
  object downRank extends MappedInt(this)
  object url extends MappedString(this,100)
  object title extends MappedString(this,100)
  object description extends MappedString(this, 300)
}

object ReditLink extends ReditLink with LongKeyedMetaMapper[ReditLink] {
	/* finds all links with a specific owner */
	def findLinksByOwner (owner : User) : List[ReditLink] = 
		ReditLink.findAll(By(ReditLink.owner, owner.id.is))
	  
	def findByTitle (title : String) : List[ReditLink] = 
		ReditLink.findAll(By(ReditLink.title, title))
	
	/* finds all links */
	def findAllLinks () : List[ReditLink] = 
		ReditLink.findAll()
}


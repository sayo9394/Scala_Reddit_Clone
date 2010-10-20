package com.redditclone.snippet

import scala.xml.{NodeSeq,Text}
import net.liftweb.http._
import net.liftweb.http.SHtml._
import net.liftweb.http.RequestVar
import net.liftweb.util.Helpers._
import net.liftweb.util.Full

import java.lang.String
import model.{ReditLink,Tag,User}

class HomePage {
	def howdy = <span>Welcome to redditClone at {new _root_.java.util.Date} <br/> Its not much, <b> but i'm still learning </b> </span> 
  
  	def display (xhtml : NodeSeq) : NodeSeq = {
	    val entries : NodeSeq = ReditLink.findAllLinks match {
	    	case Nil => Text("You have no accounts set up")
	    	case links => links.flatMap({lnk =>
	    		bind("lnk", chooseTemplate("reditLink", "entry", xhtml),
				"title" -> <a href={"/reditLink/" + lnk.title.is}>{lnk.title.is}</a>,
				"url" -> Text(lnk.url.is),
				"rank" -> Text((lnk.upRank.is-lnk.downRank.is).toString),
				"voteUp" -> Text("Vote Up"),
				"voteDown" -> Text("Vote Down"))
	    	})	
	    }
	    bind("reditLink", xhtml, "entry" -> entries)
  	}
  	
  	def detail (xhtml: NodeSeq) : NodeSeq = S.param("title") match {
	    case Full(linkTitle) => {
	        ReditLink.findByTitle(linkTitle) match {
	          case lnk :: Nil => {
	              bind("lnk", xhtml,
	                   "title" -> lnk.title.asHtml,
	                   "url" -> <a href={lnk.url.asHtml}>{lnk.url.asHtml}</a>,
	                   "description" -> lnk.description.asHtml,
	                   "rank" -> (lnk.upRank.asHtml))
	            }
	          case _ => Text("Could not locate link " + linkTitle)
	        }
	      }
	    case _ => Text("No link name provided")
 	}
}



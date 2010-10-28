package com.redditclone.snippet

import scala.xml.{NodeSeq,Text}
import net.liftweb.http._
import net.liftweb.http.SHtml._
import net.liftweb.http.RequestVar
import net.liftweb.util.Helpers._
import net.liftweb.common._

import com.redditclone.model.{ReditLink,Tag,User,Comment}

class HomePage {
	var currentLink : ReditLink = null
	def loggedIn(html: NodeSeq) =
	   if (User.loggedIn_?) html else NodeSeq.Empty
	
	def loggedOut(html: NodeSeq) =
	   if (!User.loggedIn_?) html else NodeSeq.Empty
	
	def detail (xhtml: NodeSeq) : NodeSeq = S.param("title") match {
	    case Full(linkTitle) => {
	        ReditLink.findByTitle(linkTitle) match {
	          case lnk :: Nil => {
	         	  currentLink = lnk
	         	  val entries : NodeSeq = Comment.byLink(lnk) match {
	         	  //val entries : NodeSeq = Comment.findAll() match {
						case Nil => Text("No Comments")
						case comments => comments.flatMap({cmnt =>
							bind("cmnt", chooseTemplate("lnk", "CommentEntry", xhtml),
								"title" -> Text(cmnt.title.is),
								"desc" -> Text(cmnt.description.is))
						})
					}    
	              
	         	  bind("lnk", xhtml,
	                   "title" -> lnk.title.asHtml,
	                   "url" -> <a href={"http://" + lnk.url.is}>{lnk.url.is}</a>, 	// The "http://" which is added infront of the link, forces redirection
	                   "description" -> lnk.description.asHtml,						// rules to be ignored.
	                   "rank" -> (lnk.rank.asHtml),
	                   "CommentEntry" -> entries)
	            }
	          case _ => Text("Could not locate link " + linkTitle)
	        }
	      }
	    case _ => Text("No link name provided")
 	}
	
	object currentCommentVar extends RequestVar[Comment]({
		Comment.create.owner(User.currentUser).reditLink(currentLink)
	})
	
	def currentComment = currentCommentVar.is
	def addComment (xhtml : NodeSeq) : NodeSeq = {
		def doSave () = {
			currentComment.validate match {
				case Nil =>
				currentComment.save
				//S.redirectTo("/index")
				case x => S.error(x)
			}
		}
		
		val comment = currentComment
		
		bind("comment", xhtml,
		"id" -> SHtml.hidden(() => currentCommentVar(comment)),
		"title" -> SHtml.text(currentComment.title.is, currentComment.title(_)),
		"desc"-> SHtml.text(currentComment.description.is, currentComment.description(_)),
		"save" -> SHtml.submit("Save", doSave))
	}	
}



package com.redditclone.snippet

import scala.xml.NodeSeq
import net.liftweb.http._
import net.liftweb.http.SHtml._
import net.liftweb.http.RequestVar
import net.liftweb.util.Helpers._
import net.liftweb.common._
import com.redditclone.controller._

import com.redditclone.model.{ReditLink,Tag,User}
import java.util.Date

class AddReditLink{
  object currentReditLinkVar extends RequestVar[ReditLink]({
      ReditLink.create.owner(User.currentUser.open_!)
    })
  def currentReditLink = currentReditLinkVar.is

  def addentry (xhtml : NodeSeq) : NodeSeq = {
    def doSave () = {

      currentReditLink.validate match {
        case Nil =>
          currentReditLink.save
          ReditClone.notifyListeners
          S.redirectTo("/index")
        case x => S.error(x)
      }
    }

    val reditlink = currentReditLink

    bind("reditlink", xhtml,
         "id" -> SHtml.hidden(() => currentReditLinkVar(reditlink)),
         "dateOf" -> SHtml.hidden(()=>currentReditLink.dateOf(new java.util.Date)),
         "title" -> SHtml.text(currentReditLink.title.is, currentReditLink.title(_)),
         "url" -> SHtml.text(currentReditLink.url.is, currentReditLink.url(_)),
         "desc"-> SHtml.text(currentReditLink.description.is, currentReditLink.description(_)),
         "save" -> SHtml.submit("Save", doSave))
  }	
}

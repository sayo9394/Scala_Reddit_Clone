package com.redditclone.comet

import net.liftweb.http._
import net.liftweb.http.S._
import net.liftweb.http.SHtml._
import net.liftweb.http.js.JsCmd
import net.liftweb.http.js.JsCmds._
import net.liftweb.http.js.JE._
import net.liftweb.util.Helpers._
import net.liftweb.util._
import net.liftweb.common._
import scala.xml.{NodeSeq,Text, Elem}
import com.redditclone.model._
import com.redditclone.controller._

object CurrentLinkActor extends SessionVar[Box[LinkActor]](Empty)

class LinkActor extends CometActor {
  CurrentLinkActor.set(Full(this))

    override def defaultPrefix = Full("linkactor")
    var lnkViews: List[ReditLink] = Nil
    def render = {
    	def lnkView(lnk: ReditLink): NodeSeq = {	
	  val rank = lnk.rank
          val voteUpButton: Elem = 
            User.currentUser.map(ignore => ajaxButton(S.?("Vote Up!"), () => voteUp(lnk.id))) openOr <span/>

          val voteDownButton: Elem = User.currentUser.map(ignore => ajaxButton(S.?("Vote Down!"), () => voteDown(lnk.id))) openOr <span/>
          (<div>
           <strong>Title:</strong> {lnk.title}
           <br/>
           <strong>Rank:</strong> {lnk.rank}
           <br/>
           <div>
           <a href={"/reditLink/" + urlEncode(lnk.title)}>{lnk.title}</a>: by {lnk.owner.name}
           </div>
           <div>
           {voteUpButton} {voteDownButton}
           </div>
           <strong>Description:</strong> {lnk.description}<br/><br/>
           </div>)
        }
      bind("foo" -> <div>{lnkViews.flatMap(lnkView _)}</div>)
    }

    def voteUp(id: Long): JsCmd = {
      for {
        user <- User.currentUser
      } ReditClone ! VoteUp(id, user)

      Noop
    }
    
    def voteDown(id: Long): JsCmd = {
      for {
        user <- User.currentUser
      } ReditClone ! VoteDown(id, user)

      Noop
    }

    override def localSetup {
        ReditClone !? AddListener(this) match {
            case UpdateLinks => lnkViews = ReditLink.findAllLinks
            case _ => println("Other ls")
        }
    }

    override def localShutdown {
        ReditClone ! RemoveListener(this)
    }

    override def lowPriority : PartialFunction[Any, Unit] = {
      case UpdateLinks => lnkViews = ReditLink.findAllLinks; reRender(false)
      case u: User => reRender(false);  // reRender on login
      case _ => println("Other lp")
    }
}


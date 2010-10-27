package com.redditclone.comet

import net.liftweb.http._
import net.liftweb.http.S._
import net.liftweb.http.SHtml._
import net.liftweb.http.js.JsCmd
import net.liftweb.http.js.JsCmds._
import net.liftweb.http.js.JE._
import net.liftweb.util.Helpers._
import net.liftweb.util._
import scala.xml.{NodeSeq,Text}
import com.redditclone.model._
import com.redditclone.controller._
import java.lang.Long

class LinkActor extends CometActor {
    override def defaultPrefix = Full("linkactor")
    var lnkViews: List[ReditLink] = Nil
    def render = {
    	def lnkView(lnk: ReditLink): NodeSeq = {	
	    	val rank = lnk.rank
            val voteUpButton = <button type="button">{S.?("Vote Up!")}</button> %
            	("onclick" -> ajaxCall(JsRaw(lnk.title.is), voteUp _))    
            val voteDownButton = <button type="button">{S.?("Vote Down!")}</button> %
            	("onclick" -> ajaxCall(JsRaw(lnk.title.is), voteDown _))      	
            
            (<div>
                <strong>Title:</strong> {lnk.title}
                <br/>
                <strong>Rank:</strong> {lnk.rank}
                <br/>
                <div>
                     <a href={"/reditLink/" + lnk.title.is}>{lnk.title.is}</a>: by {lnk.owner.name}
                </div>
                <div>
                   {voteUpButton} {voteDownButton}
                </div>
                   <strong>Description:</strong> {lnk.description}<br/><br/>
            </div>)
        }
        bind("foo" -> <div>{lnkViews.flatMap(lnkView _)}</div>)
    }

    def voteUp(title:String): JsCmd = {
    	val user = User.currentUser.open_!
        ReditClone ! VoteUp(title,user)
        Noop
    }
    
    def voteDown(title:String): JsCmd = {
    	val user = User.currentUser.open_!
        println("title: "+title)
    	println("user: "+user)
    	ReditClone ! VoteDown(title,user)
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
        case _ => println("Other lp")
    }
}


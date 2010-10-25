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
              ("onclick" -> voteUp)
              
            val voteDownButton = <button type="button">{S.?("Vote Down!")}</button> %
              ("onclick" -> voteDown)
            
            (<div>
                <strong>{lnk.title}</strong>
                <br/>
                <div>
                     <a href={"http://" + lnk.url.is}>{lnk.url.is}</a>: by {lnk.owner.name}
                </div>
                <div>
                   {voteUpButton} {voteDownButton}
                </div>
                {lnk.description}<br/>
            </div>)
        }
    	
       /* def lnkViews: NodeSeq = ReditLink.findAllLinks match {
            case Nil => Text("You have no accounts set up")
	    	case links => links.flatMap({lnk =>
            	bind("lnk" -> lnkView _)
	    	})
        }*/	
	    
        bind("foo" -> <div>{lnkViews.flatMap(lnkView _)}</div>)
    }

    def voteUp(): JsCmd = {
        //ReditClone ! VoteUp(title)
        Noop
    }
    
    def voteDown(): JsCmd = {
       // ReditClone ! VoteDown(title)
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


package com.redditclone.comet

import net.liftweb.http._
import net.liftweb.http.S._
import net.liftweb.http.SHtml._
import net.liftweb.http.js.JsCmd
import net.liftweb.http.js.JsCmds._
import net.liftweb.http.js.JE._
import net.liftweb.util.Helpers._
import net.liftweb.util._
import scala.xml.NodeSeq
import com.redditclone.model._
import com.redditclone.controller._
import java.lang.Long

class LinkActor extends CometActor {
    override def defaultPrefix = Full("reditLink")
    val title = S.param("title")
    def render = {
        def lnkView: NodeSeq = {
            val lnk = ReditLink.findByTitle(title)
            val rank = lnk.rank
            val voteUpButton = <button type="button">{S.?("Vote Up!")}</button> %
              ("onclick" -> ajaxCall(voteUp _))
              
            val voteDownButton = <button type="button">{S.?("Vote Down!")}</button> %
              ("onclick" -> ajaxCall(voteDown _))
            
            (<div>
                <strong>{lnk.title}</strong>
                <br/>
                <div>
                     <a href={"http://" + lnk.url.is}>{lnk.url.is}</a>: by {lnk.user.niceName}
                </div>
                <div>
                   {voteUpButton} {voteDownButton}
                </div>
                {lnk.description}<br/>
            </div>)
        }
        bind("foo" -> <div>{lnkView}</div>)
    }

    def voteUp(): JsCmd = {
        ReditClone ! VoteUp(title)
        Noop
    }
    
    def voteDown(): JsCmd = {
        ReditClone ! VoteDown(title)
        Noop
    }

    override def localSetup {
        ReditClone !? AddListener(this, this.title) match {

            case Success(true) => println("Listener added")
            case _ => println("Other ls")
        }
    }

    override def localShutdown {
        ReditClone ! RemoveListener(this, this.title)
    }

    override def lowPriority : PartialFunction[Any, Unit] = {
        case Success(true) => reRender(false)
        case _ => println("Other lp")
    }
}


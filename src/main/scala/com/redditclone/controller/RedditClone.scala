package com.redditclone.controller

import scala.actors.Actor
import scala.actors.Actor._
import scala.collection.mutable.{HashMap,HashSet}
import scala.collection.mutable.ListBuffer
import com.redditclone.model._
import net.liftweb.mapper.By

// messages
case class AddListener(listener:Actor)
case class RemoveListener(listener:Actor)
case class UpdateLinks
case class VoteUp(title:String, user:User)
case class VoteDown(title:String, user:User)
case class Success(success:Boolean)

object ReditClone extends Actor{
	val listeners = new HashSet[Actor]
	def notifyListeners = {
		listeners.foreach(_ ! UpdateLinks)
    }
	
	def act = {
		loop {
			react {
				case AddListener(listener:Actor) =>
					listeners.incl(listener)
					reply(UpdateLinks)
				case RemoveListener(listener:Actor) =>
					listeners.excl(listener)
				case VoteUp(title:String, user:User) =>
					val lnk = ReditLink.findByTitle(title).firstOption.get
					val newRank = lnk.rank + 1
					lnk.rank(newRank).save
					val rank = Rank.create
					rank.voteUp(true).lnk(lnk).owner(user).save
                    notifyListeners           
                case VoteDown(title:String, user:User) =>
                    val lnk = ReditLink.findByTitle(title).firstOption.get
					val newRank = lnk.rank - 1
					lnk.rank(newRank).save
					println("title: "+title) 
					println("rank: "+lnk.rank.is) 
					val rank = Rank.create
					rank.voteUp(false).lnk(lnk).owner(user).save
                    notifyListeners
			}
		}
	}
    start
}

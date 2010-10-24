package com.redditclone.controller

import scala.actors.Actor
import scala.actors.Actor._
import scala.collection.mutable.HashMap
import scala.collection.mutable.ListBuffer
import com.redditclone.model._
import net.liftweb.mapper.By

// messages
case class AddListener(listener:Actor, lnkId:Long)
case class RemoveListener(listener:Actor, lnkId:Long)
case class VoteUp(lnkId:Long, user:User)
case class VoteDown(lnkId:Long, user:User)
case class Success(success:Boolean)

object ReditClone extends Actor{
	val listeners = new HashMap[Long, ListBuffer[Actor]]
	def notifyListeners(lnkId:Long) = {
		if (listeners.contains(lnkId)){
            listeners(lnkId).foreach((actor) => {
                val lnk = ReditLink.findByKey(lnkId).open_!
                actor ! highRank(lnk)
            })
        }
	}
	
	def act = {
		loop {
			react {
				case AddListener(listener:Actor, lnkId:Long) =>
					if (!listeners.contains(lnkId)){
                        listeners(lnkId) = new ListBuffer[Actor]
                    }
                    listeners(lnkId) += listener
					reply(Success(true))
				case RemoveListener(listener:Actor, lnkId:Long) =>
					listeners(lnkId) -= listener
				case VoteUp(lnkId:Long, user:User) =>
                    val lnk =
                        ReditLink.findAll(By(ReditLink.id, lnkId)).firstOption.get
                    val rank = Rank.create
                    rank.voteUp(true).lnk(lnk).owner(user).save
                    notifyListeners(lnk.id)           
                case VoteDown(lnkId:Long, user:User) =>
                    val lnk =
                        ReditLink.findAll(By(ReditLink.id, lnkId)).firstOption.get
                    val rank = Rank.create
                    rank.voteUp(false).lnk(lnk).owner(user).save
                    notifyListeners(lnk.id)
			}
		}
	}
    start
}

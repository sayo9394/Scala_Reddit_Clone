package com.redditclone.controller

import scala.collection.mutable.{HashMap,HashSet}
import scala.collection.mutable.ListBuffer
import com.redditclone.model._
import net.liftweb.mapper.By
import net.liftweb.actor._

// messages
case class AddListener(listener:LiftActor)
case class RemoveListener(listener:LiftActor)
case class UpdateLinks
case class VoteUp(id: Long, user:User)
case class VoteDown(id: Long, user:User)
case class Success(success:Boolean)

object ReditClone extends LiftActor{
  private var listeners = Set[LiftActor]()
  def notifyListeners = {
    listeners.foreach(_ ! UpdateLinks)
  }

  def messageHandler = {
    case AddListener(listener) =>
      listeners += (listener)
    reply(UpdateLinks)
    
    case RemoveListener(listener) =>
      listeners -= listener

    case VoteUp(id, user) =>
      for {
        lnk <- ReditLink.find(id)
      } {
        val newRank = lnk.rank + 1
        lnk.rank(newRank).save
        Rank.create.voteUp(true).lnk(lnk).owner(user).save
        notifyListeners
      }           

    case VoteDown(id, user) =>
      for {
        lnk <- ReditLink.find(id)
      } {
        val newRank = lnk.rank - 1
        lnk.rank(newRank).save
        Rank.create.voteUp(false).lnk(lnk).owner(user).save
        notifyListeners
      }
  }
}

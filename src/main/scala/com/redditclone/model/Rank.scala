package com.redditclone.model

import net.liftweb.mapper._

object Rank extends Rank with LongKeyedMetaMapper[Rank]{
    override def dbIndexes = Index(lnk) :: Index(owner) :: super.dbIndexes
}

class Rank extends LongKeyedMapper[Rank] with IdPK{
    def getSingleton = Rank

    object voteUp extends MappedBoolean(this)
    object lnk extends MappedLongForeignKey(this, ReditLink)
    object owner extends MappedLongForeignKey(this, User)
}

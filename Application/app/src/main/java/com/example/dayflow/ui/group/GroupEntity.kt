package com.example.dayflow.ui.group

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

enum class GroupPrivacy { PRIVATE, CODE }
enum class GroupIcon { GROUP, GYM, BOOK, FIRE, HEART }

@Entity(tableName = "user_groups")
data class GroupEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val privacy: GroupPrivacy,
    val icon: GroupIcon = GroupIcon.GROUP,
    val memberCount: Int = 1,
    /** موقت: اسم اعضا با کاما جدا شده؛ بعد از اتصال به سرور جدول جدا می‌شه */
    val memberNames: String = "",
    val inviteCode: String? = null,
    val weeklyProgress: Float = 0f,
    val nextEventTitle: String? = null,
    val nextEventAt: Long? = null,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "group_invites")
data class GroupInviteEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val groupName: String,
    val inviterName: String,
    val privacy: GroupPrivacy = GroupPrivacy.PRIVATE,
    val icon: GroupIcon = GroupIcon.GROUP,
    val memberCount: Int = 1,
    val receivedAt: Long = System.currentTimeMillis()
)

@Dao
interface GroupDao {
    @Query("SELECT * FROM user_groups ORDER BY createdAt DESC")
    fun observeGroups(): Flow<List<GroupEntity>>

    @Query("SELECT * FROM group_invites ORDER BY receivedAt DESC")
    fun observeInvites(): Flow<List<GroupInviteEntity>>

    @Insert
    suspend fun insertGroup(group: GroupEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInvite(invite: GroupInviteEntity): Long

    @Query("SELECT * FROM group_invites WHERE id = :id")
    suspend fun getInvite(id: Long): GroupInviteEntity?

    @Query("DELETE FROM group_invites WHERE id = :id")
    suspend fun deleteInvite(id: Long)

    @Query("DELETE FROM user_groups WHERE id = :id")
    suspend fun deleteGroup(id: Long)
}

@Singleton
class GroupRepository @Inject constructor(
    private val dao: GroupDao
) {
    fun observeGroups(): Flow<List<GroupEntity>> = dao.observeGroups()
    fun observeInvites(): Flow<List<GroupInviteEntity>> = dao.observeInvites()

    suspend fun createGroup(name: String, privacy: GroupPrivacy, creatorName: String): Long {
        val code = if (privacy == GroupPrivacy.CODE) generateInviteCode() else null
        return dao.insertGroup(
            GroupEntity(
                name = name.trim(),
                privacy = privacy,
                memberCount = 1,
                memberNames = creatorName.trim(),
                inviteCode = code
            )
        )
    }

    suspend fun acceptInvite(inviteId: Long) {
        val invite = dao.getInvite(inviteId) ?: return
        dao.insertGroup(
            GroupEntity(
                name = invite.groupName,
                privacy = invite.privacy,
                icon = invite.icon,
                memberCount = invite.memberCount + 1,
                memberNames = invite.inviterName
            )
        )
        dao.deleteInvite(inviteId)
    }

    suspend fun rejectInvite(inviteId: Long) = dao.deleteInvite(inviteId)

    suspend fun leaveGroup(groupId: Long) = dao.deleteGroup(groupId)

    /** فقط برای تست تا قبل از اتصال به سرور */
    suspend fun addDebugInvite(invite: GroupInviteEntity) = dao.insertInvite(invite)

    private fun generateInviteCode(): String {
        val chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"
        return (1..6).map { chars.random() }.joinToString("")
    }
}


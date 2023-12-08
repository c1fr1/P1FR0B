package bot.modules

import bot.Logger
import net.dv8tion.jda.api.events.*
import net.dv8tion.jda.api.events.channel.ChannelCreateEvent
import net.dv8tion.jda.api.events.channel.ChannelDeleteEvent
import net.dv8tion.jda.api.events.channel.GenericChannelEvent
import net.dv8tion.jda.api.events.channel.update.*
import net.dv8tion.jda.api.events.guild.*
import net.dv8tion.jda.api.events.guild.invite.GenericGuildInviteEvent
import net.dv8tion.jda.api.events.guild.invite.GuildInviteCreateEvent
import net.dv8tion.jda.api.events.guild.invite.GuildInviteDeleteEvent
import net.dv8tion.jda.api.events.guild.member.*
import net.dv8tion.jda.api.events.guild.member.update.*
import net.dv8tion.jda.api.events.guild.override.GenericPermissionOverrideEvent
import net.dv8tion.jda.api.events.guild.override.PermissionOverrideCreateEvent
import net.dv8tion.jda.api.events.guild.override.PermissionOverrideDeleteEvent
import net.dv8tion.jda.api.events.guild.override.PermissionOverrideUpdateEvent
import net.dv8tion.jda.api.events.guild.update.*
import net.dv8tion.jda.api.events.guild.voice.*
import net.dv8tion.jda.api.events.http.HttpRequestEvent
import net.dv8tion.jda.api.events.interaction.*
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.events.message.*
import net.dv8tion.jda.api.events.message.react.*
import net.dv8tion.jda.api.events.role.GenericRoleEvent
import net.dv8tion.jda.api.events.role.RoleCreateEvent
import net.dv8tion.jda.api.events.role.RoleDeleteEvent
import net.dv8tion.jda.api.events.role.update.*
import net.dv8tion.jda.api.events.self.*
import net.dv8tion.jda.api.events.session.ReadyEvent
import net.dv8tion.jda.api.events.stage.GenericStageInstanceEvent
import net.dv8tion.jda.api.events.stage.StageInstanceCreateEvent
import net.dv8tion.jda.api.events.stage.StageInstanceDeleteEvent
import net.dv8tion.jda.api.events.stage.update.GenericStageInstanceUpdateEvent
import net.dv8tion.jda.api.events.stage.update.StageInstanceUpdatePrivacyLevelEvent
import net.dv8tion.jda.api.events.stage.update.StageInstanceUpdateTopicEvent
import net.dv8tion.jda.api.events.thread.GenericThreadEvent
import net.dv8tion.jda.api.events.thread.ThreadHiddenEvent
import net.dv8tion.jda.api.events.thread.ThreadRevealedEvent
import net.dv8tion.jda.api.events.thread.member.GenericThreadMemberEvent
import net.dv8tion.jda.api.events.thread.member.ThreadMemberJoinEvent
import net.dv8tion.jda.api.events.thread.member.ThreadMemberLeaveEvent
import net.dv8tion.jda.api.events.user.GenericUserEvent
import net.dv8tion.jda.api.events.user.UserActivityEndEvent
import net.dv8tion.jda.api.events.user.UserActivityStartEvent
import net.dv8tion.jda.api.events.user.UserTypingEvent
import net.dv8tion.jda.api.events.user.update.*
import net.dv8tion.jda.api.hooks.EventListener
import net.dv8tion.jda.internal.utils.ClassWalker
import java.lang.invoke.MethodHandle
import java.lang.invoke.MethodHandles
import java.lang.invoke.MethodType
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap


interface ListenerAdapter : EventListener {
	fun onGenericEvent(event: GenericEvent) {}
	fun onGenericUpdate(event: UpdateEvent<*, *>) {}
	fun onRawGateway(event: RawGatewayEvent) {}
	fun onGatewayPing(event: GatewayPingEvent) {}

	//JDA Events
	fun onReady(event: ReadyEvent) {}
	fun onStatusChange(event: StatusChangeEvent) {}
	fun onException(event: ExceptionEvent) {}

	//Interaction Events
	fun onSlashCommandInteraction(event : SlashCommandInteractionEvent) {}
	fun onUserContextInteraction(event : UserContextInteractionEvent) {}
	fun onMessageContextInteraction(event : MessageContextInteractionEvent) {}
	fun onButtonInteraction(event : ButtonInteractionEvent) {}
	fun onCommandAutoCompleteInteraction(event : CommandAutoCompleteInteractionEvent) {}
	fun onModalInteraction(event : ModalInteractionEvent) {}

	//User Events
	fun onUserUpdateName(event: UserUpdateNameEvent) {}
	fun onUserUpdateAvatar(event: UserUpdateAvatarEvent) {}
	fun onUserUpdateOnlineStatus(event: UserUpdateOnlineStatusEvent) {}
	fun onUserUpdateActivityOrder(event: UserUpdateActivityOrderEvent) {}
	fun onUserUpdateFlags(event: UserUpdateFlagsEvent) {}
	fun onUserTyping(event: UserTypingEvent) {}
	fun onUserActivityStart(event: UserActivityStartEvent) {}
	fun onUserActivityEnd(event: UserActivityEndEvent) {}
	fun onUserUpdateActivities(event: UserUpdateActivitiesEvent) {}

	//Self Events. Fires only in relation to the currently logged in account.
	fun onSelfUpdateAvatar(event: SelfUpdateAvatarEvent) {}
	fun onSelfUpdateMFA(event: SelfUpdateMFAEvent) {}
	fun onSelfUpdateName(event: SelfUpdateNameEvent) {}
	fun onSelfUpdateVerified(event: SelfUpdateVerifiedEvent) {}

	//Message Events
	fun onMessageReceived(event: MessageReceivedEvent) {}
	fun onMessageUpdate(event: MessageUpdateEvent) {}
	fun onMessageDelete(event: MessageDeleteEvent) {}
	fun onMessageBulkDelete(event: MessageBulkDeleteEvent) {}
	fun onMessageEmbed(event: MessageEmbedEvent) {}
	fun onMessageReactionAdd(event: MessageReactionAddEvent) {}
	fun onMessageReactionRemove(event: MessageReactionRemoveEvent) {}
	fun onMessageReactionRemoveAll(event: MessageReactionRemoveAllEvent) {}

	//PermissionOverride Events
	fun onPermissionOverrideDelete(event: PermissionOverrideDeleteEvent) {}
	fun onPermissionOverrideUpdate(event: PermissionOverrideUpdateEvent) {}
	fun onPermissionOverrideCreate(event: PermissionOverrideCreateEvent) {}

	//StageInstance Event
	fun onStageInstanceDelete(event: StageInstanceDeleteEvent) {}
	fun onStageInstanceUpdateTopic(event: StageInstanceUpdateTopicEvent) {}
	fun onStageInstanceUpdatePrivacyLevel(event: StageInstanceUpdatePrivacyLevelEvent) {}
	fun onStageInstanceCreate(event: StageInstanceCreateEvent) {}

	//Channel Events
	fun onChannelCreate(event: ChannelCreateEvent) {}
	fun onChannelDelete(event: ChannelDeleteEvent) {}

	//Channel Update Events
	fun onChannelUpdateBitrate(event: ChannelUpdateBitrateEvent) {}
	fun onChannelUpdateName(event: ChannelUpdateNameEvent) {}
	fun onChannelUpdateNSFW(event: ChannelUpdateNSFWEvent) {}
	fun onChannelUpdateParent(event: ChannelUpdateParentEvent) {}
	fun onChannelUpdatePosition(event: ChannelUpdatePositionEvent) {}
	fun onChannelUpdateRegion(event: ChannelUpdateRegionEvent) {}
	fun onChannelUpdateSlowmode(event: ChannelUpdateSlowmodeEvent) {}
	fun onChannelUpdateTopic(event: ChannelUpdateTopicEvent) {}
	fun onChannelUpdateType(event: ChannelUpdateTypeEvent) {}
	fun onChannelUpdateUserLimit(event: ChannelUpdateUserLimitEvent) {}
	fun onChannelUpdateArchived(event: ChannelUpdateArchivedEvent) {}
	fun onChannelUpdateArchiveTimestamp(event: ChannelUpdateArchiveTimestampEvent) {}
	fun onChannelUpdateAutoArchiveDuration(event: ChannelUpdateAutoArchiveDurationEvent) {}
	fun onChannelUpdateLocked(event: ChannelUpdateLockedEvent) {}
	fun onChannelUpdateInvitable(event: ChannelUpdateInvitableEvent) {}

	//Thread Events
	fun onThreadRevealed(event: ThreadRevealedEvent) {}
	fun onThreadHidden(event: ThreadHiddenEvent) {}

	//Thread Member Events
	fun onThreadMemberJoin(event: ThreadMemberJoinEvent) {}
	fun onThreadMemberLeave(event: ThreadMemberLeaveEvent) {}

	//Guild Events
	fun onGuildReady(event: GuildReadyEvent) {}
	fun onGuildTimeout(event: GuildTimeoutEvent) {}
	fun onGuildJoin(event: GuildJoinEvent) {}
	fun onGuildLeave(event: GuildLeaveEvent) {}
	fun onGuildAvailable(event: GuildAvailableEvent) {}
	fun onGuildUnavailable(event: GuildUnavailableEvent) {}
	fun onUnavailableGuildJoined(event: UnavailableGuildJoinedEvent) {}
	fun onUnavailableGuildLeave(event: UnavailableGuildLeaveEvent) {}
	fun onGuildBan(event: GuildBanEvent) {}
	fun onGuildUnban(event: GuildUnbanEvent) {}
	fun onGuildMemberRemove(event: GuildMemberRemoveEvent) {}

	//Guild Update Events
	fun onGuildUpdateAfkChannel(event: GuildUpdateAfkChannelEvent) {}
	fun onGuildUpdateSystemChannel(event: GuildUpdateSystemChannelEvent) {}
	fun onGuildUpdateRulesChannel(event: GuildUpdateRulesChannelEvent) {}
	fun onGuildUpdateCommunityUpdatesChannel(event: GuildUpdateCommunityUpdatesChannelEvent) {}
	fun onGuildUpdateAfkTimeout(event: GuildUpdateAfkTimeoutEvent) {}
	fun onGuildUpdateExplicitContentLevel(event: GuildUpdateExplicitContentLevelEvent) {}
	fun onGuildUpdateIcon(event: GuildUpdateIconEvent) {}
	fun onGuildUpdateMFALevel(event: GuildUpdateMFALevelEvent) {}
	fun onGuildUpdateName(event: GuildUpdateNameEvent) {}
	fun onGuildUpdateNotificationLevel(event: GuildUpdateNotificationLevelEvent) {}
	fun onGuildUpdateOwner(event: GuildUpdateOwnerEvent) {}
	fun onGuildUpdateSplash(event: GuildUpdateSplashEvent) {}
	fun onGuildUpdateVerificationLevel(event: GuildUpdateVerificationLevelEvent) {}
	fun onGuildUpdateLocale(event: GuildUpdateLocaleEvent) {}
	fun onGuildUpdateFeatures(event: GuildUpdateFeaturesEvent) {}
	fun onGuildUpdateVanityCode(event: GuildUpdateVanityCodeEvent) {}
	fun onGuildUpdateBanner(event: GuildUpdateBannerEvent) {}
	fun onGuildUpdateDescription(event: GuildUpdateDescriptionEvent) {}
	fun onGuildUpdateBoostTier(event: GuildUpdateBoostTierEvent) {}
	fun onGuildUpdateBoostCount(event: GuildUpdateBoostCountEvent) {}
	fun onGuildUpdateMaxMembers(event: GuildUpdateMaxMembersEvent) {}
	fun onGuildUpdateMaxPresences(event: GuildUpdateMaxPresencesEvent) {}
	fun onGuildUpdateNSFWLevel(event: GuildUpdateNSFWLevelEvent) {}

	//Guild Invite Events
	fun onGuildInviteCreate(event: GuildInviteCreateEvent) {}
	fun onGuildInviteDelete(event: GuildInviteDeleteEvent) {}

	//Guild Member Events
	fun onGuildMemberJoin(event: GuildMemberJoinEvent) {}
	fun onGuildMemberRoleAdd(event: GuildMemberRoleAddEvent) {}
	fun onGuildMemberRoleRemove(event: GuildMemberRoleRemoveEvent) {}

	//Guild Member Update Events
	fun onGuildMemberUpdate(event: GuildMemberUpdateEvent) {}
	fun onGuildMemberUpdateNickname(event: GuildMemberUpdateNicknameEvent) {}
	fun onGuildMemberUpdateAvatar(event: GuildMemberUpdateAvatarEvent) {}
	fun onGuildMemberUpdateBoostTime(event: GuildMemberUpdateBoostTimeEvent) {}
	fun onGuildMemberUpdatePending(event: GuildMemberUpdatePendingEvent) {}

	//Guild Voice Events
	fun onGuildVoiceUpdate(event: GuildVoiceUpdateEvent) {}
	fun onGuildVoiceMute(event: GuildVoiceMuteEvent) {}
	fun onGuildVoiceDeafen(event: GuildVoiceDeafenEvent) {}
	fun onGuildVoiceGuildMute(event: GuildVoiceGuildMuteEvent) {}
	fun onGuildVoiceGuildDeafen(event: GuildVoiceGuildDeafenEvent) {}
	fun onGuildVoiceSelfMute(event: GuildVoiceSelfMuteEvent) {}
	fun onGuildVoiceSelfDeafen(event: GuildVoiceSelfDeafenEvent) {}
	fun onGuildVoiceSuppress(event: GuildVoiceSuppressEvent) {}
	fun onGuildVoiceStream(event: GuildVoiceStreamEvent) {}
	fun onGuildVoiceVideo(event: GuildVoiceVideoEvent) {}
	fun onGuildVoiceRequestToSpeak(event: GuildVoiceRequestToSpeakEvent) {}

	//Role events
	fun onRoleCreate(event: RoleCreateEvent) {}
	fun onRoleDelete(event: RoleDeleteEvent) {}

	//Role Update Events
	fun onRoleUpdateColor(event: RoleUpdateColorEvent) {}
	fun onRoleUpdateHoisted(event: RoleUpdateHoistedEvent) {}
	fun onRoleUpdateIcon(event: RoleUpdateIconEvent) {}
	fun onRoleUpdateMentionable(event: RoleUpdateMentionableEvent) {}
	fun onRoleUpdateName(event: RoleUpdateNameEvent) {}
	fun onRoleUpdatePermissions(event: RoleUpdatePermissionsEvent) {}
	fun onRoleUpdatePosition(event: RoleUpdatePositionEvent) {}

	// Debug Events
	fun onHttpRequest(event: HttpRequestEvent) {}

	//Generic Events
	fun onGenericInteractionCreate(event: GenericInteractionCreateEvent) {}
	fun onGenericMessage(event: GenericMessageEvent) {}
	fun onGenericMessageReaction(event: GenericMessageReactionEvent) {}
	fun onGenericUser(event: GenericUserEvent) {}
	fun onGenericUserPresence(event: GenericUserPresenceEvent) {}
	fun onGenericSelfUpdate(event: GenericSelfUpdateEvent<*>) {}
	fun onGenericStageInstance(event: GenericStageInstanceEvent) {}
	fun onGenericStageInstanceUpdate(event: GenericStageInstanceUpdateEvent<*>) {}
	fun onGenericChannel(event: GenericChannelEvent) {}
	fun onGenericChannelUpdate(event: GenericChannelUpdateEvent<*>) {}
	fun onGenericThread(event: GenericThreadEvent) {}
	fun onGenericThreadMember(event: GenericThreadMemberEvent) {}
	fun onGenericGuild(event: GenericGuildEvent) {}
	fun onGenericGuildUpdate(event: GenericGuildUpdateEvent<*>) {}
	fun onGenericGuildInvite(event: GenericGuildInviteEvent) {}
	fun onGenericGuildMember(event: GenericGuildMemberEvent) {}
	fun onGenericGuildMemberUpdate(event: GenericGuildMemberUpdateEvent<*>) {}
	fun onGenericGuildVoice(event: GenericGuildVoiceEvent) {}
	fun onGenericRole(event: GenericRoleEvent) {}
	fun onGenericRoleUpdate(event: GenericRoleUpdateEvent<*>) {}
	fun onGenericPermissionOverride(event: GenericPermissionOverrideEvent) {}

	companion object {
		private val lookup = MethodHandles.lookup()
		private val methods: ConcurrentMap<Class<*>, MethodHandle?> = ConcurrentHashMap()
		private var unresolved: MutableSet<Class<*>> = ConcurrentHashMap.newKeySet()
		private fun findMethod(clazz: Class<*>): MethodHandle? {
			var name = clazz.simpleName
			val type: MethodType = MethodType.methodType(Void.TYPE, clazz)
			try {
				name = "on" + name.substring(0, name.length - "Event".length)
				return lookup.findVirtual(ListenerAdapter::class.java, name, type)
			} catch (ignored: NoSuchMethodException) {
			} // this means this is probably a custom event!
			catch (ignored: IllegalAccessException) {
			}
			return null
		}

		init {
			Collections.addAll(
				unresolved,
				Any::class.java,  // Objects aren't events
				Event::class.java,  // onEvent is final and would never be found
				UpdateEvent::class.java,  // onGenericUpdate has already been called
				GenericEvent::class.java // onGenericEvent has already been called
			)
		}
	}

	override fun onEvent(event: GenericEvent) {
		try {
			onGenericEvent(event)
			if (event is UpdateEvent<*, *>) onGenericUpdate(event)
			for (clazz in ClassWalker.range(event.javaClass, GenericEvent::class.java)) {
				if (unresolved.contains(clazz)) continue
				val mh = methods.computeIfAbsent(clazz) { clasz: Class<*> ->
					findMethod(
						clasz
					)
				}
				if (mh == null) {
					unresolved.add(clazz)
					continue
				}
				try {
					mh.invoke(this, event)
				} catch (throwable: Throwable) {
					if (throwable is RuntimeException) throw throwable
					if (throwable is Error) throw throwable
					throw IllegalStateException(throwable)
				}
			}
		} catch (e : Throwable) {
			Logger.logError(e)
		}
	}
}
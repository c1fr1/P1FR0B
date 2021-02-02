package bot.modules

import bot.Logger
import net.dv8tion.jda.api.events.*
import net.dv8tion.jda.api.events.channel.category.CategoryCreateEvent
import net.dv8tion.jda.api.events.channel.category.CategoryDeleteEvent
import net.dv8tion.jda.api.events.channel.category.GenericCategoryEvent
import net.dv8tion.jda.api.events.channel.category.update.CategoryUpdateNameEvent
import net.dv8tion.jda.api.events.channel.category.update.CategoryUpdatePositionEvent
import net.dv8tion.jda.api.events.channel.category.update.GenericCategoryUpdateEvent
import net.dv8tion.jda.api.events.channel.priv.PrivateChannelCreateEvent
import net.dv8tion.jda.api.events.channel.priv.PrivateChannelDeleteEvent
import net.dv8tion.jda.api.events.channel.store.GenericStoreChannelEvent
import net.dv8tion.jda.api.events.channel.store.StoreChannelCreateEvent
import net.dv8tion.jda.api.events.channel.store.StoreChannelDeleteEvent
import net.dv8tion.jda.api.events.channel.store.update.GenericStoreChannelUpdateEvent
import net.dv8tion.jda.api.events.channel.store.update.StoreChannelUpdateNameEvent
import net.dv8tion.jda.api.events.channel.store.update.StoreChannelUpdatePositionEvent
import net.dv8tion.jda.api.events.channel.text.GenericTextChannelEvent
import net.dv8tion.jda.api.events.channel.text.TextChannelCreateEvent
import net.dv8tion.jda.api.events.channel.text.TextChannelDeleteEvent
import net.dv8tion.jda.api.events.channel.text.update.*
import net.dv8tion.jda.api.events.channel.voice.GenericVoiceChannelEvent
import net.dv8tion.jda.api.events.channel.voice.VoiceChannelCreateEvent
import net.dv8tion.jda.api.events.channel.voice.VoiceChannelDeleteEvent
import net.dv8tion.jda.api.events.channel.voice.update.*
import net.dv8tion.jda.api.events.emote.EmoteAddedEvent
import net.dv8tion.jda.api.events.emote.EmoteRemovedEvent
import net.dv8tion.jda.api.events.emote.GenericEmoteEvent
import net.dv8tion.jda.api.events.emote.update.EmoteUpdateNameEvent
import net.dv8tion.jda.api.events.emote.update.EmoteUpdateRolesEvent
import net.dv8tion.jda.api.events.emote.update.GenericEmoteUpdateEvent
import net.dv8tion.jda.api.events.guild.*
import net.dv8tion.jda.api.events.guild.invite.GenericGuildInviteEvent
import net.dv8tion.jda.api.events.guild.invite.GuildInviteCreateEvent
import net.dv8tion.jda.api.events.guild.invite.GuildInviteDeleteEvent
import net.dv8tion.jda.api.events.guild.member.*
import net.dv8tion.jda.api.events.guild.member.update.GenericGuildMemberUpdateEvent
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateBoostTimeEvent
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateNicknameEvent
import net.dv8tion.jda.api.events.guild.override.GenericPermissionOverrideEvent
import net.dv8tion.jda.api.events.guild.override.PermissionOverrideCreateEvent
import net.dv8tion.jda.api.events.guild.override.PermissionOverrideDeleteEvent
import net.dv8tion.jda.api.events.guild.override.PermissionOverrideUpdateEvent
import net.dv8tion.jda.api.events.guild.update.*
import net.dv8tion.jda.api.events.guild.voice.*
import net.dv8tion.jda.api.events.http.HttpRequestEvent
import net.dv8tion.jda.api.events.message.*
import net.dv8tion.jda.api.events.message.guild.*
import net.dv8tion.jda.api.events.message.guild.react.*
import net.dv8tion.jda.api.events.message.priv.*
import net.dv8tion.jda.api.events.message.priv.react.GenericPrivateMessageReactionEvent
import net.dv8tion.jda.api.events.message.priv.react.PrivateMessageReactionAddEvent
import net.dv8tion.jda.api.events.message.priv.react.PrivateMessageReactionRemoveEvent
import net.dv8tion.jda.api.events.message.react.*
import net.dv8tion.jda.api.events.role.GenericRoleEvent
import net.dv8tion.jda.api.events.role.RoleCreateEvent
import net.dv8tion.jda.api.events.role.RoleDeleteEvent
import net.dv8tion.jda.api.events.role.update.*
import net.dv8tion.jda.api.events.self.*
import net.dv8tion.jda.api.events.user.GenericUserEvent
import net.dv8tion.jda.api.events.user.UserActivityEndEvent
import net.dv8tion.jda.api.events.user.UserActivityStartEvent
import net.dv8tion.jda.api.events.user.UserTypingEvent
import net.dv8tion.jda.api.events.user.update.*
import net.dv8tion.jda.api.hooks.EventListener

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//
interface ListenerAdapter : EventListener {

	fun onGenericEvent(event: GenericEvent) {}
	fun onGenericUpdate(event: UpdateEvent<*, *>) {}
	fun onRawGateway(event: RawGatewayEvent) {}
	fun onGatewayPing(event: GatewayPingEvent) {}
	fun onReady(event: ReadyEvent) {}
	fun onResume(event: ResumedEvent) {}
	fun onReconnect(event: ReconnectedEvent) {}
	fun onDisconnect(event: DisconnectEvent) {}
	fun onShutdown(event: ShutdownEvent) {}
	fun onStatusChange(event: StatusChangeEvent) {}
	fun onException(event: ExceptionEvent) {}
	fun onUserUpdateName(event: UserUpdateNameEvent) {}
	fun onUserUpdateDiscriminator(event: UserUpdateDiscriminatorEvent) {}
	fun onUserUpdateAvatar(event: UserUpdateAvatarEvent) {}
	fun onUserUpdateOnlineStatus(event: UserUpdateOnlineStatusEvent) {}
	fun onUserUpdateActivityOrder(event: UserUpdateActivityOrderEvent) {}
	fun onUserUpdateFlags(event: UserUpdateFlagsEvent) {}
	fun onUserTyping(event: UserTypingEvent) {}
	fun onUserActivityStart(event: UserActivityStartEvent) {}
	fun onUserActivityEnd(event: UserActivityEndEvent) {}
	fun onSelfUpdateAvatar(event: SelfUpdateAvatarEvent) {}
	fun onSelfUpdateMFA(event: SelfUpdateMFAEvent) {}
	fun onSelfUpdateName(event: SelfUpdateNameEvent) {}
	fun onSelfUpdateVerified(event: SelfUpdateVerifiedEvent) {}
	fun onGuildMessageReceived(event: GuildMessageReceivedEvent) {}
	fun onGuildMessageUpdate(event: GuildMessageUpdateEvent) {}
	fun onGuildMessageDelete(event: GuildMessageDeleteEvent) {}
	fun onGuildMessageEmbed(event: GuildMessageEmbedEvent) {}
	fun onGuildMessageReactionAdd(event: GuildMessageReactionAddEvent) {}
	fun onGuildMessageReactionRemove(event: GuildMessageReactionRemoveEvent) {}
	fun onGuildMessageReactionRemoveAll(event: GuildMessageReactionRemoveAllEvent) {}
	fun onGuildMessageReactionRemoveEmote(event: GuildMessageReactionRemoveEmoteEvent) {}
	fun onPrivateMessageReceived(event: PrivateMessageReceivedEvent) {}
	fun onPrivateMessageUpdate(event: PrivateMessageUpdateEvent) {}
	fun onPrivateMessageDelete(event: PrivateMessageDeleteEvent) {}
	fun onPrivateMessageEmbed(event: PrivateMessageEmbedEvent) {}
	fun onPrivateMessageReactionAdd(event: PrivateMessageReactionAddEvent) {}
	fun onPrivateMessageReactionRemove(event: PrivateMessageReactionRemoveEvent) {}
	fun onMessageReceived(event: MessageReceivedEvent) {}
	fun onMessageUpdate(event: MessageUpdateEvent) {}
	fun onMessageDelete(event: MessageDeleteEvent) {}
	fun onMessageBulkDelete(event: MessageBulkDeleteEvent) {}
	fun onMessageEmbed(event: MessageEmbedEvent) {}
	fun onMessageReactionAdd(event: MessageReactionAddEvent) {}
	fun onMessageReactionRemove(event: MessageReactionRemoveEvent) {}
	fun onMessageReactionRemoveAll(event: MessageReactionRemoveAllEvent) {}
	fun onMessageReactionRemoveEmote(event: MessageReactionRemoveEmoteEvent) {}
	fun onPermissionOverrideDelete(event: PermissionOverrideDeleteEvent) {}
	fun onPermissionOverrideUpdate(event: PermissionOverrideUpdateEvent) {}
	fun onPermissionOverrideCreate(event: PermissionOverrideCreateEvent) {}
	fun onStoreChannelDelete(event: StoreChannelDeleteEvent) {}
	fun onStoreChannelUpdateName(event: StoreChannelUpdateNameEvent) {}
	fun onStoreChannelUpdatePosition(event: StoreChannelUpdatePositionEvent) {}
	fun onStoreChannelCreate(event: StoreChannelCreateEvent) {}
	fun onTextChannelDelete(event: TextChannelDeleteEvent) {}
	fun onTextChannelUpdateName(event: TextChannelUpdateNameEvent) {}
	fun onTextChannelUpdateTopic(event: TextChannelUpdateTopicEvent) {}
	fun onTextChannelUpdatePosition(event: TextChannelUpdatePositionEvent) {}
	fun onTextChannelUpdateNSFW(event: TextChannelUpdateNSFWEvent) {}
	fun onTextChannelUpdateParent(event: TextChannelUpdateParentEvent) {}
	fun onTextChannelUpdateSlowmode(event: TextChannelUpdateSlowmodeEvent) {}
	fun onTextChannelCreate(event: TextChannelCreateEvent) {}
	fun onVoiceChannelDelete(event: VoiceChannelDeleteEvent) {}
	fun onVoiceChannelUpdateName(event: VoiceChannelUpdateNameEvent) {}
	fun onVoiceChannelUpdatePosition(event: VoiceChannelUpdatePositionEvent) {}
	fun onVoiceChannelUpdateUserLimit(event: VoiceChannelUpdateUserLimitEvent) {}
	fun onVoiceChannelUpdateBitrate(event: VoiceChannelUpdateBitrateEvent) {}
	fun onVoiceChannelUpdateParent(event: VoiceChannelUpdateParentEvent) {}
	fun onVoiceChannelCreate(event: VoiceChannelCreateEvent) {}
	fun onCategoryDelete(event: CategoryDeleteEvent) {}
	fun onCategoryUpdateName(event: CategoryUpdateNameEvent) {}
	fun onCategoryUpdatePosition(event: CategoryUpdatePositionEvent) {}
	fun onCategoryCreate(event: CategoryCreateEvent) {}
	fun onPrivateChannelCreate(event: PrivateChannelCreateEvent) {}
	fun onPrivateChannelDelete(event: PrivateChannelDeleteEvent) {}
	fun onGuildReady(event: GuildReadyEvent) {}
	fun onGuildJoin(event: GuildJoinEvent) {}
	fun onGuildLeave(event: GuildLeaveEvent) {}
	fun onGuildAvailable(event: GuildAvailableEvent) {}
	fun onGuildUnavailable(event: GuildUnavailableEvent) {}
	fun onUnavailableGuildJoined(event: UnavailableGuildJoinedEvent) {}
	fun onUnavailableGuildLeave(event: UnavailableGuildLeaveEvent) {}
	fun onGuildBan(event: GuildBanEvent) {}
	fun onGuildUnban(event: GuildUnbanEvent) {}
	fun onGuildMemberRemove(event: GuildMemberRemoveEvent) {}
	fun onGuildUpdateAfkChannel(event: GuildUpdateAfkChannelEvent) {}
	fun onGuildUpdateSystemChannel(event: GuildUpdateSystemChannelEvent) {}
	fun onGuildUpdateAfkTimeout(event: GuildUpdateAfkTimeoutEvent) {}
	fun onGuildUpdateExplicitContentLevel(event: GuildUpdateExplicitContentLevelEvent) {}
	fun onGuildUpdateIcon(event: GuildUpdateIconEvent) {}
	fun onGuildUpdateMFALevel(event: GuildUpdateMFALevelEvent) {}
	fun onGuildUpdateName(event: GuildUpdateNameEvent) {}
	fun onGuildUpdateNotificationLevel(event: GuildUpdateNotificationLevelEvent) {}
	fun onGuildUpdateOwner(event: GuildUpdateOwnerEvent) {}
	fun onGuildUpdateRegion(event: GuildUpdateRegionEvent) {}
	fun onGuildUpdateSplash(event: GuildUpdateSplashEvent) {}
	fun onGuildUpdateVerificationLevel(event: GuildUpdateVerificationLevelEvent) {}
	fun onGuildUpdateFeatures(event: GuildUpdateFeaturesEvent) {}
	fun onGuildUpdateVanityCode(event: GuildUpdateVanityCodeEvent) {}
	fun onGuildUpdateBanner(event: GuildUpdateBannerEvent) {}
	fun onGuildUpdateDescription(event: GuildUpdateDescriptionEvent) {}
	fun onGuildUpdateBoostTier(event: GuildUpdateBoostTierEvent) {}
	fun onGuildUpdateBoostCount(event: GuildUpdateBoostCountEvent) {}
	fun onGuildUpdateMaxMembers(event: GuildUpdateMaxMembersEvent) {}
	fun onGuildUpdateMaxPresences(event: GuildUpdateMaxPresencesEvent) {}
	fun onGuildInviteCreate(event: GuildInviteCreateEvent) {}
	fun onGuildInviteDelete(event: GuildInviteDeleteEvent) {}
	fun onGuildMemberJoin(event: GuildMemberJoinEvent) {}
	fun onGuildMemberRoleAdd(event: GuildMemberRoleAddEvent) {}
	fun onGuildMemberRoleRemove(event: GuildMemberRoleRemoveEvent) {}
	fun onGuildMemberUpdateNickname(event: GuildMemberUpdateNicknameEvent) {}
	fun onGuildMemberUpdateBoostTime(event: GuildMemberUpdateBoostTimeEvent) {}
	fun onGuildVoiceUpdate(event: GuildVoiceUpdateEvent) {}
	fun onGuildVoiceJoin(event: GuildVoiceJoinEvent) {}
	fun onGuildVoiceMove(event: GuildVoiceMoveEvent) {}
	fun onGuildVoiceLeave(event: GuildVoiceLeaveEvent) {}
	fun onGuildVoiceMute(event: GuildVoiceMuteEvent) {}
	fun onGuildVoiceDeafen(event: GuildVoiceDeafenEvent) {}
	fun onGuildVoiceGuildMute(event: GuildVoiceGuildMuteEvent) {}
	fun onGuildVoiceGuildDeafen(event: GuildVoiceGuildDeafenEvent) {}
	fun onGuildVoiceSelfMute(event: GuildVoiceSelfMuteEvent) {}
	fun onGuildVoiceSelfDeafen(event: GuildVoiceSelfDeafenEvent) {}
	fun onGuildVoiceSuppress(event: GuildVoiceSuppressEvent) {}
	fun onGuildVoiceStream(event: GuildVoiceStreamEvent) {}
	fun onRoleCreate(event: RoleCreateEvent) {}
	fun onRoleDelete(event: RoleDeleteEvent) {}
	fun onRoleUpdateColor(event: RoleUpdateColorEvent) {}
	fun onRoleUpdateHoisted(event: RoleUpdateHoistedEvent) {}
	fun onRoleUpdateMentionable(event: RoleUpdateMentionableEvent) {}
	fun onRoleUpdateName(event: RoleUpdateNameEvent) {}
	fun onRoleUpdatePermissions(event: RoleUpdatePermissionsEvent) {}
	fun onRoleUpdatePosition(event: RoleUpdatePositionEvent) {}
	fun onEmoteAdded(event: EmoteAddedEvent) {}
	fun onEmoteRemoved(event: EmoteRemovedEvent) {}
	fun onEmoteUpdateName(event: EmoteUpdateNameEvent) {}
	fun onEmoteUpdateRoles(event: EmoteUpdateRolesEvent) {}
	fun onHttpRequest(event: HttpRequestEvent) {}
	fun onGenericMessage(event: GenericMessageEvent) {}
	fun onGenericMessageReaction(event: GenericMessageReactionEvent) {}
	fun onGenericGuildMessage(event: GenericGuildMessageEvent) {}
	fun onGenericGuildMessageReaction(event: GenericGuildMessageReactionEvent) {}
	fun onGenericPrivateMessage(event: GenericPrivateMessageEvent) {}
	fun onGenericPrivateMessageReaction(event: GenericPrivateMessageReactionEvent) {}
	fun onGenericUser(event: GenericUserEvent) {}
	fun onGenericUserPresence(event: GenericUserPresenceEvent) {}
	fun onGenericSelfUpdate(event: GenericSelfUpdateEvent<*>) {}
	fun onGenericStoreChannel(event: GenericStoreChannelEvent) {}
	fun onGenericStoreChannelUpdate(event: GenericStoreChannelUpdateEvent<*>) {}
	fun onGenericTextChannel(event: GenericTextChannelEvent) {}
	fun onGenericTextChannelUpdate(event: GenericTextChannelUpdateEvent<*>) {}
	fun onGenericVoiceChannel(event: GenericVoiceChannelEvent) {}
	fun onGenericVoiceChannelUpdate(event: GenericVoiceChannelUpdateEvent<*>) {}
	fun onGenericCategory(event: GenericCategoryEvent) {}
	fun onGenericCategoryUpdate(event: GenericCategoryUpdateEvent<*>) {}
	fun onGenericGuild(event: GenericGuildEvent) {}
	fun onGenericGuildUpdate(event: GenericGuildUpdateEvent<*>) {}
	fun onGenericGuildInvite(event: GenericGuildInviteEvent) {}
	fun onGenericGuildMember(event: GenericGuildMemberEvent) {}
	fun onGenericGuildMemberUpdate(event: GenericGuildMemberUpdateEvent<*>) {}
	fun onGenericGuildVoice(event: GenericGuildVoiceEvent) {}
	fun onGenericRole(event: GenericRoleEvent) {}
	fun onGenericRoleUpdate(event: GenericRoleUpdateEvent<*>) {}
	fun onGenericEmote(event: GenericEmoteEvent) {}
	fun onGenericEmoteUpdate(event: GenericEmoteUpdateEvent<*>) {}
	fun onGenericPermissionOverride(event: GenericPermissionOverrideEvent) {}
	override fun onEvent(event: GenericEvent) {
		try {
			onGenericEvent(event)

			when (event) {
				is UpdateEvent<*, *> -> onGenericUpdate(event)
				is RawGatewayEvent -> onRawGateway(event)
			}

			when (event) {
				is ReadyEvent -> onReady(event)
				is ResumedEvent -> onResume(event)
				is ReconnectedEvent -> onReconnect(event)
				is DisconnectEvent -> onDisconnect(event)
				is ShutdownEvent -> onShutdown(event)
				is StatusChangeEvent -> onStatusChange(event)
				is ExceptionEvent -> onException(event)
				is GatewayPingEvent -> onGatewayPing(event)
				is GuildMessageReceivedEvent -> onGuildMessageReceived(event)
				is GuildMessageUpdateEvent -> onGuildMessageUpdate(event)
				is GuildMessageDeleteEvent -> onGuildMessageDelete(event)
				is GuildMessageEmbedEvent -> onGuildMessageEmbed(event)
				is GuildMessageReactionAddEvent -> onGuildMessageReactionAdd(event)
				is GuildMessageReactionRemoveEvent -> onGuildMessageReactionRemove(event)
				is GuildMessageReactionRemoveAllEvent -> onGuildMessageReactionRemoveAll(event)
				is GuildMessageReactionRemoveEmoteEvent -> onGuildMessageReactionRemoveEmote(event)
				is PrivateMessageReceivedEvent -> onPrivateMessageReceived(event)
				is PrivateMessageUpdateEvent -> onPrivateMessageUpdate(event)
				is PrivateMessageDeleteEvent -> onPrivateMessageDelete(event)
				is PrivateMessageEmbedEvent -> onPrivateMessageEmbed(event)
				is PrivateMessageReactionAddEvent -> onPrivateMessageReactionAdd(event)
				is PrivateMessageReactionRemoveEvent -> onPrivateMessageReactionRemove(event)
				is MessageReceivedEvent -> onMessageReceived(event)
				is MessageUpdateEvent -> onMessageUpdate(event)
				is MessageDeleteEvent -> onMessageDelete(event)
				is MessageBulkDeleteEvent -> onMessageBulkDelete(event)
				is MessageEmbedEvent -> onMessageEmbed(event)
				is MessageReactionAddEvent -> onMessageReactionAdd(event)
				is MessageReactionRemoveEvent -> onMessageReactionRemove(event)
				is MessageReactionRemoveAllEvent -> onMessageReactionRemoveAll(event)
				is MessageReactionRemoveEmoteEvent -> onMessageReactionRemoveEmote(event)
				is UserUpdateNameEvent -> onUserUpdateName(event)
				is UserUpdateDiscriminatorEvent -> onUserUpdateDiscriminator(event)
				is UserUpdateAvatarEvent -> onUserUpdateAvatar(event)
				is UserUpdateActivityOrderEvent -> onUserUpdateActivityOrder(event)
				is UserUpdateOnlineStatusEvent -> onUserUpdateOnlineStatus(event)
				is UserTypingEvent -> onUserTyping(event)
				is UserActivityStartEvent -> onUserActivityStart(event)
				is UserActivityEndEvent -> onUserActivityEnd(event)
				is UserUpdateFlagsEvent -> onUserUpdateFlags(event)
				is SelfUpdateAvatarEvent -> onSelfUpdateAvatar(event)
				is SelfUpdateMFAEvent -> onSelfUpdateMFA(event)
				is SelfUpdateNameEvent -> onSelfUpdateName(event)
				is SelfUpdateVerifiedEvent -> onSelfUpdateVerified(event)
				is PermissionOverrideDeleteEvent -> onPermissionOverrideDelete(event)
				is PermissionOverrideUpdateEvent -> onPermissionOverrideUpdate(event)
				is PermissionOverrideCreateEvent -> onPermissionOverrideCreate(event)
				is StoreChannelCreateEvent -> onStoreChannelCreate(event)
				is StoreChannelDeleteEvent -> onStoreChannelDelete(event)
				is StoreChannelUpdateNameEvent -> onStoreChannelUpdateName(event)
				is StoreChannelUpdatePositionEvent -> onStoreChannelUpdatePosition(event)
				is TextChannelCreateEvent -> onTextChannelCreate(event)
				is TextChannelUpdateNameEvent -> onTextChannelUpdateName(event)
				is TextChannelUpdateTopicEvent -> onTextChannelUpdateTopic(event)
				is TextChannelUpdatePositionEvent -> onTextChannelUpdatePosition(event)
				is TextChannelUpdateNSFWEvent -> onTextChannelUpdateNSFW(event)
				is TextChannelUpdateParentEvent -> onTextChannelUpdateParent(event)
				is TextChannelUpdateSlowmodeEvent -> onTextChannelUpdateSlowmode(event)
				is TextChannelDeleteEvent -> onTextChannelDelete(event)
				is VoiceChannelCreateEvent -> onVoiceChannelCreate(event)
				is VoiceChannelUpdateNameEvent -> onVoiceChannelUpdateName(event)
				is VoiceChannelUpdatePositionEvent -> onVoiceChannelUpdatePosition(event)
				is VoiceChannelUpdateUserLimitEvent -> onVoiceChannelUpdateUserLimit(event)
				is VoiceChannelUpdateBitrateEvent -> onVoiceChannelUpdateBitrate(event)
				is VoiceChannelUpdateParentEvent -> onVoiceChannelUpdateParent(event)
				is VoiceChannelDeleteEvent -> onVoiceChannelDelete(event)
				is CategoryCreateEvent -> onCategoryCreate(event)
				is CategoryUpdateNameEvent -> onCategoryUpdateName(event)
				is CategoryUpdatePositionEvent -> onCategoryUpdatePosition(event)
				is CategoryDeleteEvent -> onCategoryDelete(event)
				is PrivateChannelCreateEvent -> onPrivateChannelCreate(event)
				is PrivateChannelDeleteEvent -> onPrivateChannelDelete(event)
				is GuildReadyEvent -> onGuildReady(event)
				is GuildJoinEvent -> onGuildJoin(event)
				is GuildLeaveEvent -> onGuildLeave(event)
				is GuildAvailableEvent -> onGuildAvailable(event)
				is GuildUnavailableEvent -> onGuildUnavailable(event)
				is UnavailableGuildJoinedEvent -> onUnavailableGuildJoined(event)
				is UnavailableGuildLeaveEvent -> onUnavailableGuildLeave(event)
				is GuildBanEvent -> onGuildBan(event)
				is GuildUnbanEvent -> onGuildUnban(event)
				is GuildMemberRemoveEvent -> onGuildMemberRemove(event)
				is GuildUpdateAfkChannelEvent -> onGuildUpdateAfkChannel(event)
				is GuildUpdateSystemChannelEvent -> onGuildUpdateSystemChannel(event)
				is GuildUpdateAfkTimeoutEvent -> onGuildUpdateAfkTimeout(event)
				is GuildUpdateExplicitContentLevelEvent -> onGuildUpdateExplicitContentLevel(event)
				is GuildUpdateIconEvent -> onGuildUpdateIcon(event)
				is GuildUpdateMFALevelEvent -> onGuildUpdateMFALevel(event)
				is GuildUpdateNameEvent -> onGuildUpdateName(event)
				is GuildUpdateNotificationLevelEvent -> onGuildUpdateNotificationLevel(event)
				is GuildUpdateOwnerEvent -> onGuildUpdateOwner(event)
				is GuildUpdateRegionEvent -> onGuildUpdateRegion(event)
				is GuildUpdateSplashEvent -> onGuildUpdateSplash(event)
				is GuildUpdateVerificationLevelEvent -> onGuildUpdateVerificationLevel(event)
				is GuildUpdateFeaturesEvent -> onGuildUpdateFeatures(event)
				is GuildUpdateVanityCodeEvent -> onGuildUpdateVanityCode(event)
				is GuildUpdateBannerEvent -> onGuildUpdateBanner(event)
				is GuildUpdateDescriptionEvent -> onGuildUpdateDescription(event)
				is GuildUpdateBoostTierEvent -> onGuildUpdateBoostTier(event)
				is GuildUpdateBoostCountEvent -> onGuildUpdateBoostCount(event)
				is GuildUpdateMaxMembersEvent -> onGuildUpdateMaxMembers(event)
				is GuildUpdateMaxPresencesEvent -> onGuildUpdateMaxPresences(event)
				is GuildInviteCreateEvent -> onGuildInviteCreate(event)
				is GuildInviteDeleteEvent -> onGuildInviteDelete(event)
				is GuildMemberJoinEvent -> onGuildMemberJoin(event)
				is GuildMemberRoleAddEvent -> onGuildMemberRoleAdd(event)
				is GuildMemberRoleRemoveEvent -> onGuildMemberRoleRemove(event)
				is GuildMemberUpdateNicknameEvent -> onGuildMemberUpdateNickname(event)
				is GuildMemberUpdateBoostTimeEvent -> onGuildMemberUpdateBoostTime(event)
				is GuildVoiceJoinEvent -> onGuildVoiceJoin(event)
				is GuildVoiceMoveEvent -> onGuildVoiceMove(event)
				is GuildVoiceLeaveEvent -> onGuildVoiceLeave(event)
				is GuildVoiceMuteEvent -> onGuildVoiceMute(event)
				is GuildVoiceDeafenEvent -> onGuildVoiceDeafen(event)
				is GuildVoiceGuildMuteEvent -> onGuildVoiceGuildMute(event)
				is GuildVoiceGuildDeafenEvent -> onGuildVoiceGuildDeafen(event)
				is GuildVoiceSelfMuteEvent -> onGuildVoiceSelfMute(event)
				is GuildVoiceSelfDeafenEvent -> onGuildVoiceSelfDeafen(event)
				is GuildVoiceSuppressEvent -> onGuildVoiceSuppress(event)
				is GuildVoiceStreamEvent -> onGuildVoiceStream(event)
				is RoleCreateEvent -> onRoleCreate(event)
				is RoleDeleteEvent -> onRoleDelete(event)
				is RoleUpdateColorEvent -> onRoleUpdateColor(event)
				is RoleUpdateHoistedEvent -> onRoleUpdateHoisted(event)
				is RoleUpdateMentionableEvent -> onRoleUpdateMentionable(event)
				is RoleUpdateNameEvent -> onRoleUpdateName(event)
				is RoleUpdatePermissionsEvent -> onRoleUpdatePermissions(event)
				is RoleUpdatePositionEvent -> onRoleUpdatePosition(event)
				is EmoteAddedEvent -> onEmoteAdded(event)
				is EmoteRemovedEvent -> onEmoteRemoved(event)
				is EmoteUpdateNameEvent -> onEmoteUpdateName(event)
				is EmoteUpdateRolesEvent -> onEmoteUpdateRoles(event)
				is HttpRequestEvent -> onHttpRequest(event)
			}
			if (event is GuildVoiceUpdateEvent) {
				onGuildVoiceUpdate(event)
			}
			when (event) {
				is GenericMessageReactionEvent -> onGenericMessageReaction(event)
				is GenericPrivateMessageReactionEvent -> onGenericPrivateMessageReaction(event)
				is GenericStoreChannelUpdateEvent<*> -> onGenericStoreChannelUpdate(event)
				is GenericTextChannelUpdateEvent<*> -> onGenericTextChannelUpdate(event)
				is GenericCategoryUpdateEvent<*> -> onGenericCategoryUpdate(event)
				is GenericGuildMessageReactionEvent -> onGenericGuildMessageReaction(event)
				is GenericVoiceChannelUpdateEvent<*> -> onGenericVoiceChannelUpdate(event)
				is GenericGuildUpdateEvent<*> -> onGenericGuildUpdate(event)
				is GenericGuildMemberUpdateEvent<*> -> onGenericGuildMemberUpdate(event)
				is GenericGuildVoiceEvent -> onGenericGuildVoice(event)
				is GenericRoleUpdateEvent<*> -> onGenericRoleUpdate(event)
				is GenericEmoteUpdateEvent<*> -> onGenericEmoteUpdate(event)
				is GenericUserPresenceEvent -> onGenericUserPresence(event)
				is GenericPermissionOverrideEvent -> onGenericPermissionOverride(event)
			}
			when (event) {
				is GenericMessageEvent -> onGenericMessage(event)
				is GenericPrivateMessageEvent -> onGenericPrivateMessage(event)
				is GenericGuildMessageEvent -> onGenericGuildMessage(event)
				is GenericGuildInviteEvent -> onGenericGuildInvite(event)
				is GenericGuildMemberEvent -> onGenericGuildMember(event)
				is GenericUserEvent -> onGenericUser(event)
				is GenericSelfUpdateEvent<*> -> onGenericSelfUpdate(event)
				is GenericStoreChannelEvent -> onGenericStoreChannel(event)
				is GenericTextChannelEvent -> onGenericTextChannel(event)
				is GenericVoiceChannelEvent -> onGenericVoiceChannel(event)
				is GenericCategoryEvent -> onGenericCategory(event)
				is GenericRoleEvent -> onGenericRole(event)
				is GenericEmoteEvent -> onGenericEmote(event)
			}

			if (event is GenericGuildEvent) {
				onGenericGuild(event)
			}
		} catch (e : Throwable) {
			Logger.logError(e)
		}
	}
}
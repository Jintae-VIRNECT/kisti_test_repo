<template>
  <popover
    title
    trigger="click"
    placement="bottom-end"
    :width="340"
    popper-class="popover-myInfo"
  >
    <profile slot="header" :onError="onImageError"></profile>

    <div
      class="popover-myInfo--link privacy"
      v-if="account.userType !== 'SubUser' && !isTabletChrome"
    >
      <button @click="goAdmin()">VIRNECT Admin</button>
      <!--
        <anchor :link="'/admin/groups/group?type=user&groupid='+currentGroup.groupId" target="_blank">VIRNECT Admin</anchor>
      -->
    </div>

    <div class="popover-myInfo--link language">
      <button class="ui-anchor" @click="showLangs = true">
        {{ $t('service.sidebar_language') }}
      </button>
      <span class="current">{{ $t('language_abbr') }}</span>
    </div>
    <div
      class="popover-myInfo--link help"
      v-if="account.userType !== 'SubUser' && !isTabletChrome"
    >
      <button @click="goHelp()">
        {{ $t('service.sidebar_help_center') }}
      </button>
    </div>
    <div class="popover-myInfo--link logout">
      <button @click="doLogout()">
        {{ $t('service.sidebar_myInfo_logout') }}
      </button>
    </div>
    <div class="popover-myInfo--link version">
      <p class="version">{{ `web v.${version}` }}</p>
    </div>

    <div slot="reference" class="profile__button">
      <button class="button">
        <img :src="account.profile" alt="user image" @error="onImageError" />
      </button>
      <span
        class="status"
        :class="{
          online: 'wait' === viewStatus,
          busy: 'call' === viewStatus,
        }"
      ></span>
    </div>
  </popover>
</template>

<script>
import Profile from 'Profile'
export default {
  name: 'ServiceSidebarMyInfo',
  components: {
    Profile,
  },
  data() {
    return {}
  },
  computed: {},
  methods: {},

  /* Lifecycles */
  mounted() {},
  beforeDestroy() {},
}
</script>

<template>
  <div class="the-sidebar__collapse--default">
    <div class="the-sidebar__collapse__header">
      <img src="~assets/images/icon/ic-supervisor-account.svg" />
      <h5>{{ $t('menu.members') }}</h5>
    </div>
    <div class="the-sidebar__collapse__body">
      <nuxt-link to="/members" @click.native="$emit('closeCollapse')">
        <span>{{ $t('menu.collapse.members.list') }}</span>
      </nuxt-link>
      <nuxt-link to="/members/activity" @click.native="$emit('closeCollapse')">
        <span>{{ $t('menu.collapse.members.activity') }}</span>
      </nuxt-link>
    </div>
  </div>
</template>

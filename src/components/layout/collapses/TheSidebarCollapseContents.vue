<template>
  <div class="the-sidebar__collapse--default">
    <div class="the-sidebar__collapse__header">
      <img src="~assets/images/icon/ic-content.svg" />
      <h5>{{ $t('menu.collapse.contents.title') }}</h5>
    </div>
    <div class="the-sidebar__collapse__body">
      <nuxt-link to="/contents" @click.native="$emit('closeCollapse')">
        <span>{{ $t('menu.collapse.content.list') }}</span>
      </nuxt-link>
    </div>
  </div>
</template>

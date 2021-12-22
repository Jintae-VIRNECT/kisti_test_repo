<template>
  <div class="the-sidebar__collapse--default">
    <div class="the-sidebar__collapse__header">
      <img src="~assets/images/icon/ic-project.svg" />
      <h5>{{ $t('menu.collapse.projects.title') }}</h5>
    </div>
    <div class="the-sidebar__collapse__body">
      <nuxt-link to="/projects" @click.native="$emit('closeCollapse')">
        <span>{{ $t('menu.collapse.projects.list') }}</span>
      </nuxt-link>
    </div>
  </div>
</template>

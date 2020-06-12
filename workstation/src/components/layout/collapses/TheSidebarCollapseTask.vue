<template>
  <div class="the-sidebar__collapse--default">
    <div class="the-sidebar__collapse__header">
      <img src="~assets/images/icon/ic-library-books.svg" />
      <h5>{{ $t('menu.tasks') }}</h5>
    </div>
    <div class="the-sidebar__collapse__body">
      <nuxt-link to="/tasks" @click.native="$emit('closeCollapse')">
        <span>{{ $t('menu.collapse.tasks.list') }}</span>
      </nuxt-link>
      <nuxt-link to="/tasks/results" @click.native="$emit('closeCollapse')">
        <span>{{ $t('menu.collapse.tasks.results') }}</span>
      </nuxt-link>
      <nuxt-link to="/tasks/troubles" @click.native="$emit('closeCollapse')">
        <span>{{ $t('menu.collapse.tasks.troubles') }}</span>
      </nuxt-link>
    </div>
  </div>
</template>

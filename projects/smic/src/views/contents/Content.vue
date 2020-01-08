<template lang="pug">
  div
    page-header(submenu='콘텐츠')
    div
      router-link(to='/contents/new')
        el-button(type='primary' tag="el-button") 공정 등록
      router-link(to='/contents')
        el-button(type='primary' tag="el-button") 전체 목록
    router-view
</template>

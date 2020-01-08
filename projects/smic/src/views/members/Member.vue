<template lang="pug">
  div
    page-header(submenu='멤버')
    router-link(to='/members/new')
      el-button(type='primary' tag="el-button") 멤버 생성
    router-link(to='/members')
      el-button(type='primary' tag="el-button") 멤버 목록
    router-view
</template>

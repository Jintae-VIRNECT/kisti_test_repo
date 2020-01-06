<template lang="pug">
  div
    the-page-header(submenu='멤버')
    router-link(to='/members/add')
      el-button(type='primary' tag="el-button") 멤버 생성
    router-link(to='/members')
      el-button(type='primary' tag="el-button") 멤버 목록
    router-view
</template>

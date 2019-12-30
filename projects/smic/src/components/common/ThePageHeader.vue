<template lang="pug">
  div
    el-breadcrumb(separator='/')
      el-breadcrumb-item(:to="{ path: '/' }") homepage
      el-breadcrumb-item
        a(href='/') promotion management
      el-breadcrumb-item promotion list
      el-breadcrumb-item promotion detail
    h1 {{title}}
</template>
<script>
export default {
	props: ['title'],
}
</script>

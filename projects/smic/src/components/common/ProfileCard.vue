<template lang="pug">
  el-card.box-card
    .clearfix(slot='header')
      span {{profileData.username}}
      el-button(style='float: right; padding: 3px 0' type='text') Operation button
    .text.item(v-for='o in 4' :key='o')
</template>

<script>
export default {
	props: ['profileData'],
}
</script>

<template lang="pug">
  el-card.box-card
    .clearfix(slot='header')
      span {{profileData.username}}
    .text.item(v-for='o in 4' :key='o')
</template>

<script>
export default {
	props: ['profileData'],
}
</script>

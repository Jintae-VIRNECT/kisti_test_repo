<template>
  <div id="members">
    <div v-for="member in members" :key="member.id">
      <p>{{ member }}</p>
    </div>
  </div>
</template>

<script>
import memberService from '@/services/member'

export default {
  async asyncData() {
    return {
      members: await memberService.getDefaultMembersList(),
    }
  },
}
</script>

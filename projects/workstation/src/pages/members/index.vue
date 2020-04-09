<template>
  <div id="members">
    <h2>Members</h2>
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

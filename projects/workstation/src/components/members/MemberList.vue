<template>
  <el-row :gutter="20">
    <el-col :span="6" v-for="(ud, index) in memberList" :key="index">
      <!-- <member-profile-card :profileData="ud"></member-profile-card> -->
    </el-col>
  </el-row>
</template>

<script>
import { mapGetters } from 'vuex'

// import MemberProfileCard from '@/components/members/MemberProfileCard.vue'

export default {
  components: {
    // MemberProfileCard,
  },
  props: ['query'],
  computed: {
    ...mapGetters(['memberList']),
  },
}
</script>

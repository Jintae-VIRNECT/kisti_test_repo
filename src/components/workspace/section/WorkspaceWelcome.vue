<template>
  <section class="workspace-welcome">
    <div class="workspace-welcome__body">
      <p class="workspace-welcome__group">
        리모트원격솔루션 <role :role="'Manager'"></role>
      </p>
      <p class="workspace-welcome__name">리모트님, 반갑습니다.</p>
      <button class="btn">원격 협업 생성</button>
    </div>
  </section>
</template>

<script>
import Role from 'Role'
export default {
  name: 'WorkspaceWelcome',
  components: {
    Role,
  },
  data() {
    return {}
  },
  computed: {},
  watch: {},
  methods: {},

  /* Lifecycles */
  mounted() {},
}
</script>

<template lang="pug">
  div
    h1.admin-body__header 워크스페이스 관리 | {{submenu}}
</template>
<script>
export default {
	props: ['submenu'],
}
</script>

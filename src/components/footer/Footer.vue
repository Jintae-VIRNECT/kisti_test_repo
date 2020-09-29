<template>
  <footer class="footer">
    <p>
      ©VIRNECT CO., LTD. All rights reserved.
    </p>
  </footer>
</template>

<script>
export default {}
</script>

<style lang="scss">
.footer {
  position: fixed;
  bottom: 0;
  left: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 3.5714rem;
  background: #3c4049;
  & > p {
    color: #a1a6b0;
    font-weight: 500;
    font-size: 0.8571rem;
    text-align: center;
  }
}
</style>

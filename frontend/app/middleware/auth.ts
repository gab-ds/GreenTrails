export default defineNuxtRouteMiddleware((to) => {
  const auth = useAuthStore()
  if (!auth.isLoggedIn) {
    return navigateTo('/login')
  }
  const role = to.meta.role as string | undefined
  if (role) {
    const hasRole = auth.user?.ruolo === role
    if (!hasRole) {
      return navigateTo('/')
    }
  }
})

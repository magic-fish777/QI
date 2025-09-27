import router from './router'
import store from './store'
import { Message } from 'element-ui'
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'
import { getToken } from '@/utils/auth'
import { isPathMatch } from '@/utils/validate'
import { isRelogin } from '@/utils/request'

NProgress.configure({ showSpinner: false })

const whiteList = ['/login', '/register', '/chat/login', '/chat/register']
const chatProtectedPaths = ['/chat', '/chat/benefits', '/chat/profile', '/chat/settings']

const isWhiteList = (path) => {
  return whiteList.some(pattern => isPathMatch(pattern, path))
}

const isChatProtectedPath = (path) => {
  return chatProtectedPaths.some(pattern => isPathMatch(pattern, path))
}

// 检查聊天系统登录状态
const checkChatLoginStatus = () => {
  const chatToken = localStorage.getItem('chat_token')
  const isChatLoggedIn = localStorage.getItem('chat_isLoggedIn')
  return chatToken && isChatLoggedIn === 'true'
}

router.beforeEach((to, from, next) => {
  console.log('🔥 [路由守卫] 开始处理路由')
  console.log('📍 目标路径:', to.path)
  console.log('📍 来源路径:', from.path)
  console.log('📍 完整路径:', to.fullPath)

  NProgress.start()

  // 处理前台系统路径的特殊逻辑（聊天和AI）
  console.log('🔍 检查是否为前台路径 (chat 或 ai)')
  const isFrontendPath = to.path.startsWith('/chat/') || to.path === '/chat' || to.path.startsWith('/ai/') || to.path === '/ai'
  console.log('🔍 是否为前台路径:', isFrontendPath)

  if (to.path.startsWith('/chat/') || to.path === '/chat' ) {
    console.log('✅ 进入聊天路径处理逻辑')

    const isChatLoggedIn = checkChatLoginStatus()
    console.log('🔐 前台登录状态:', isChatLoggedIn)
    console.log('🔐 chat_token:', localStorage.getItem('chat_token'))
    console.log('🔐 chat_isLoggedIn:', localStorage.getItem('chat_isLoggedIn'))

    // 如果访问聊天登录页面且已经登录，重定向到聊天页面
    const isLoginPage = to.path === '/chat/login' || to.path === '/chat/register'
    console.log('🔍 是否为登录页面:', isLoginPage)

    if (isLoginPage && isChatLoggedIn) {
      console.log('🔄 已登录用户访问登录页，重定向到 /chat')
      next('/chat')
      NProgress.done()
      return
    }

    // 如果访问需要登录的聊天页面但未登录，重定向到登录页面
    const isProtectedPath = isChatProtectedPath(to.path)
    console.log('🔍 是否为保护路径:', isProtectedPath)
    console.log('🔍 保护路径列表:', chatProtectedPaths)

    if (isProtectedPath && !isChatLoggedIn) {
      console.log('🚫 访问保护路径但未登录，重定向到 /chat/login')
      next('/chat/login')
      NProgress.done()
      return
    }

    // 聊天路径且符合条件，直接放行
    console.log('✅ 聊天路径放行')
    next()
    NProgress.done()
    return
  }

  // 检查是否为AI路径
  if (to.path.startsWith('/ai/') || to.path === '/ai') {
    console.log('🤖 进入AI路径处理逻辑')

    const isChatLoggedIn = checkChatLoginStatus()
    console.log('🔐 AI路径-前台登录状态:', isChatLoggedIn)
    console.log('🔐 AI路径-chat_token:', localStorage.getItem('chat_token'))
    console.log('🔐 AI路径-chat_isLoggedIn:', localStorage.getItem('chat_isLoggedIn'))

    const isProtectedPath = isChatProtectedPath(to.path)
    console.log('🔍 AI路径-是否为保护路径:', isProtectedPath)

    if (isProtectedPath && !isChatLoggedIn) {
      console.log('🚫 AI路径-访问保护路径但未登录，重定向到 /chat/login')
      next('/chat/login')
      NProgress.done()
      return
    }

    console.log('✅ AI路径放行')
    next()
    NProgress.done()
    return
  }

  // 原有的后台系统路由守卫逻辑
  console.log('🏢 进入后台系统路由守卫逻辑')

  const hasAdminToken = getToken()
  console.log('🔐 后台token状态:', hasAdminToken)

  if (hasAdminToken) {
    console.log('✅ 有后台token，进入后台逻辑')
    to.meta.title && store.dispatch('settings/setTitle', to.meta.title)

    if (to.path === '/login') {
      console.log('🔄 后台登录页重定向到 /admin')
      next({ path: '/admin' })
      NProgress.done()
    } else if (isWhiteList(to.path)) {
      console.log('✅ 在白名单中，直接放行')
      console.log('🔍 白名单:', whiteList)
      next()
    } else {
      console.log('🔍 检查用户角色信息')
      console.log('🔍 用户角色:', store.getters.roles)

      if (store.getters.roles.length === 0) {
        console.log('📝 角色为空，获取用户信息')
        isRelogin.show = true

        store.dispatch('GetInfo').then(() => {
          console.log('✅ 获取用户信息成功')
          isRelogin.show = false
          store.dispatch('GenerateRoutes').then(accessRoutes => {
            console.log('✅ 生成路由成功:', accessRoutes)
            router.addRoutes(accessRoutes)
            next({ ...to, replace: true })
          })
        }).catch(err => {
          console.log('❌ 获取用户信息失败:', err)
          store.dispatch('LogOut').then(() => {
            Message.error(err)
            next({ path: '/admin' })
          })
        })
      } else {
        console.log('✅ 已有角色信息，直接放行')
        next()
      }
    }
  } else {
    console.log('❌ 无后台token')

    const isInWhiteList = isWhiteList(to.path)
    console.log('🔍 是否在白名单:', isInWhiteList)
    console.log('🔍 白名单:', whiteList)

    if (isInWhiteList) {
      console.log('✅ 在免登录白名单，直接进入')
      next()
    } else {
      console.log('🚫 不在白名单，重定向到后台登录页')
      next(`/login?redirect=${encodeURIComponent(to.fullPath)}`)
      NProgress.done()
    }
  }
})

router.afterEach(() => {
  NProgress.done()
})

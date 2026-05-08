import { Moon, Sun, User } from 'lucide-react'
import { useTheme } from 'next-themes'

import { NotificationDropdown } from '@/components/notifications/NotificationDropdown'
import { Button } from '@/components/ui/button'
import { useThemeStore } from '@/store/theme.store'

export function Topbar() {
  const { theme, setTheme: setNextTheme } = useTheme()
  const syncTheme = useThemeStore((state) => state.setTheme)

  return (
    <header className="flex items-center justify-between border-b px-4 py-3 md:px-6 bg-white dark:bg-slate-900 sticky top-0 z-50">
      <div className="hidden md:block text-sm font-medium text-slate-500">
        Personal finance workspace
      </div>
      
      <div className="flex items-center gap-2">
        <NotificationDropdown />
        
        <div className="h-6 w-px bg-slate-200 mx-2" />
        
        <button
          type="button"
          className="inline-flex items-center gap-2 rounded-full border border-slate-200 px-3 py-1.5 text-xs font-medium text-slate-600 hover:bg-slate-50 transition-colors"
          onClick={() => {
            const nextTheme = theme === 'dark' ? 'light' : 'dark'
            setNextTheme(nextTheme)
            syncTheme(nextTheme)
          }}
        >
          {theme === 'dark' ? <Sun className="h-3.5 w-3.5" /> : <Moon className="h-3.5 w-3.5" />}
          {theme === 'dark' ? 'Light' : 'Dark'}
        </button>

        <Button variant="ghost" size="icon" className="h-9 w-9 rounded-full bg-slate-100 border border-slate-200">
          <User className="h-4 w-4 text-slate-600" />
        </Button>
      </div>
    </header>
  )
}

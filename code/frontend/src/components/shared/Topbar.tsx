import { Moon, Sun } from 'lucide-react'
import { useTheme } from 'next-themes'

import { useThemeStore } from '@/store/theme.store'

export function Topbar() {
  const { theme, setTheme: setNextTheme } = useTheme()
  const syncTheme = useThemeStore((state) => state.setTheme)

  return (
    <header className="flex items-center justify-between border-b px-4 py-3 md:px-6">
      <div className="text-sm text-muted-foreground">Personal finance workspace</div>
      <button
        type="button"
        className="inline-flex items-center gap-2 rounded-md border px-3 py-2 text-sm"
        onClick={() => {
          const nextTheme = theme === 'dark' ? 'light' : 'dark'
          setNextTheme(nextTheme)
          syncTheme(nextTheme)
        }}
      >
        {theme === 'dark' ? <Sun className="h-4 w-4" /> : <Moon className="h-4 w-4" />}
        Theme
      </button>
    </header>
  )
}

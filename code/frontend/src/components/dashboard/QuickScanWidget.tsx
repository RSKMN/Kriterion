import { Receipt, Upload, Zap } from 'lucide-react'
import { useNavigate } from 'react-router-dom'

import { Button } from '@/components/ui/button'

export function QuickScanWidget() {
  const navigate = useNavigate()

  return (
    <div className="relative group overflow-hidden rounded-2xl border border-indigo-100 bg-gradient-to-br from-indigo-50/50 to-white p-6 transition-all hover:shadow-lg hover:shadow-indigo-500/5">
      <div className="absolute top-0 right-0 -mt-4 -mr-4 h-24 w-24 rounded-full bg-indigo-500/5 transition-transform group-hover:scale-150" />
      
      <div className="relative space-y-4">
        <div className="flex items-center gap-3">
          <div className="flex h-10 w-10 items-center justify-center rounded-xl bg-indigo-600 text-white shadow-lg shadow-indigo-200">
            <Zap className="h-5 w-5" />
          </div>
          <div>
            <h3 className="font-bold text-slate-900">Quick Scan</h3>
            <p className="text-xs text-slate-500">Automate transaction entry</p>
          </div>
        </div>

        <p className="text-sm text-slate-600 leading-relaxed">
          Skip the manual forms. Upload a receipt and let Kriterion extract the details for you.
        </p>

        <div className="flex gap-2 pt-2">
          <Button 
            className="flex-1 bg-slate-900 hover:bg-slate-800"
            onClick={() => navigate('/receipts')}
          >
            <Receipt className="mr-2 h-4 w-4" />
            Start Scan
          </Button>
          <Button 
            variant="outline" 
            className="px-3 border-slate-200 hover:bg-slate-50"
            onClick={() => navigate('/receipts')}
          >
            <Upload className="h-4 w-4" />
          </Button>
        </div>
      </div>

      <div className="mt-4 pt-4 border-t border-indigo-50 flex items-center justify-between text-[10px] font-bold text-indigo-400 uppercase tracking-widest">
        <span>OCR Engine Ready</span>
        <div className="flex h-1.5 w-1.5">
          <span className="animate-ping absolute inline-flex h-1.5 w-1.5 rounded-full bg-indigo-400 opacity-75"></span>
          <span className="relative inline-flex rounded-full h-1.5 w-1.5 bg-indigo-500"></span>
        </div>
      </div>
    </div>
  )
}

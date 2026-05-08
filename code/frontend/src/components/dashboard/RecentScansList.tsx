import { format } from 'date-fns'
import { Receipt, CheckCircle2, AlertCircle, ArrowRight } from 'lucide-react'
import { useNavigate } from 'react-router-dom'

import { Badge } from '@/components/ui/badge'
import { Button } from '@/components/ui/button'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { useOCRStore } from '@/store/ocr.store'


export function RecentScansList() {
  const navigate = useNavigate()
  const { recentScans } = useOCRStore()

  if (recentScans.length === 0) return null

  return (
    <Card className="border-slate-100 shadow-sm overflow-hidden">
      <CardHeader className="flex flex-row items-center justify-between py-4 bg-slate-50/50">
        <CardTitle className="text-sm font-bold uppercase tracking-widest text-slate-500">
          Recent Scans
        </CardTitle>
        <Button variant="ghost" size="sm" className="text-xs h-7 px-2 hover:bg-white" onClick={() => navigate('/receipts')}>
          View All <ArrowRight className="ml-1 h-3 w-3" />
        </Button>
      </CardHeader>
      <CardContent className="p-0">
        <div className="divide-y divide-slate-100">
          {recentScans.slice(0, 5).map((scan) => (
            <div key={scan.id} className="p-4 hover:bg-slate-50/50 transition-colors flex items-center justify-between gap-4">
              <div className="flex items-center gap-3">
                <div className="h-10 w-10 rounded-lg bg-indigo-50 flex items-center justify-center text-indigo-600 border border-indigo-100/50">
                  <Receipt className="h-5 w-5" />
                </div>
                <div className="min-w-0">
                  <p className="text-sm font-semibold truncate text-slate-900">{scan.merchantName}</p>
                  <p className="text-[10px] text-slate-400 font-medium">
                    {format(new Date(scan.timestamp), 'MMM d, h:mm a')}
                  </p>
                </div>
              </div>
              
              <div className="flex flex-col items-end gap-1">
                <p className="text-sm font-bold text-slate-900">${scan.amount.toFixed(2)}</p>
                <div className="flex items-center gap-1">
                  {scan.status === 'completed' ? (
                    <Badge variant="outline" className="text-[9px] py-0 px-1.5 h-4 bg-emerald-50 text-emerald-600 border-emerald-100">
                      <CheckCircle2 className="mr-1 h-2 w-2" /> Verified
                    </Badge>
                  ) : (
                    <Badge variant="outline" className="text-[9px] py-0 px-1.5 h-4 bg-amber-50 text-amber-600 border-amber-100">
                      <AlertCircle className="mr-1 h-2 w-2" /> Pending
                    </Badge>
                  )}
                </div>
              </div>
            </div>
          ))}
        </div>
      </CardContent>
    </Card>
  )
}

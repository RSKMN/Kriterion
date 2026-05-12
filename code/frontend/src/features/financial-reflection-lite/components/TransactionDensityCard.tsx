import React from 'react';
import { TransactionDensityAnalysis } from '@/services/financial-reflection.service';
import { Activity, Zap } from 'lucide-react';

interface TransactionDensityCardProps {
  analysis: TransactionDensityAnalysis;
}

export const TransactionDensityCard: React.FC<TransactionDensityCardProps> = ({ analysis }) => {
  const getDensityColor = () => {
    switch (analysis.densityLevel) {
      case 'low': return 'text-slate-500';
      case 'moderate': return 'text-emerald-600';
      case 'high': return 'text-amber-600';
      case 'very_high': return 'text-red-600';
      default: return 'text-gray-400';
    }
  };

  return (
    <div className="bg-white rounded-lg border border-gray-200 p-6 h-full flex flex-col">
      <div className="flex items-start justify-between mb-4">
        <div>
          <h3 className="text-lg font-semibold text-gray-900 flex items-center gap-2">
            <Activity className="w-5 h-5 text-orange-600" />
            Transaction Density
          </h3>
          <p className="text-sm text-gray-500 mt-1">Frequency and clustering</p>
        </div>
        <div className={`text-xs font-bold uppercase tracking-wider ${getDensityColor()}`}>
          {analysis.densityLevel} density
        </div>
      </div>

      <div className="space-y-4 flex-grow">
        <div className="grid grid-cols-2 gap-4">
          <div className="p-3 bg-slate-50 rounded-lg">
            <p className="text-[10px] text-gray-500 font-bold uppercase tracking-wider mb-1">Avg / Day</p>
            <p className="text-xl font-bold text-gray-900">{analysis.averageTransactionsPerDay}</p>
          </div>
          <div className="p-3 bg-slate-50 rounded-lg">
            <p className="text-[10px] text-gray-500 font-bold uppercase tracking-wider mb-1">Avg / Week</p>
            <p className="text-xl font-bold text-gray-900">{analysis.averageTransactionsPerWeek}</p>
          </div>
        </div>

        {analysis.hasBurstPatterns && (
          <div className="flex items-center gap-3 p-3 bg-amber-50 rounded-lg border border-amber-100">
            <Zap className="w-4 h-4 text-amber-600" />
            <div>
              <p className="text-xs font-bold text-amber-900 uppercase tracking-tight">Burst Activity Detected</p>
              <p className="text-[11px] text-amber-700">High clustering in transaction events</p>
            </div>
          </div>
        )}

        <div className="mt-auto pt-4 p-3 bg-slate-50 rounded border border-slate-100">
          <p className="text-sm text-gray-700 italic leading-snug">
            {analysis.densityObservation || '—'}
          </p>
        </div>
      </div>
    </div>
  );
};

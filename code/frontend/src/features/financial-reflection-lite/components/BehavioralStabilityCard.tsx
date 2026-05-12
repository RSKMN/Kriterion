import React from 'react';
import { BehavioralStabilityAnalysis } from '@/services/financial-reflection.service';
import { TrendingUp, Activity, ShieldCheck, AlertCircle } from 'lucide-react';

interface BehavioralStabilityCardProps {
  analysis: BehavioralStabilityAnalysis;
}

export const BehavioralStabilityCard: React.FC<BehavioralStabilityCardProps> = ({ analysis }) => {
  const getStabilityIcon = () => {
    switch (analysis.stabilityLevel) {
      case 'stable': return <ShieldCheck className="w-4 h-4 text-emerald-500" />;
      case 'shifting': return <Activity className="w-4 h-4 text-amber-500" />;
      case 'recovering': return <TrendingUp className="w-4 h-4 text-emerald-500" />;
      case 'volatile': return <AlertCircle className="w-4 h-4 text-red-500" />;
      default: return null;
    }
  };

  const getStabilityColor = () => {
    switch (analysis.stabilityLevel) {
      case 'stable': return 'text-emerald-600';
      case 'shifting': return 'text-amber-600';
      case 'recovering': return 'text-emerald-600';
      case 'volatile': return 'text-red-600';
      default: return 'text-gray-400';
    }
  };

  return (
    <div className="bg-white rounded-lg border border-gray-200 p-6 h-full flex flex-col">
      <div className="flex items-start justify-between mb-4">
        <div>
          <h3 className="text-lg font-semibold text-gray-900 flex items-center gap-2">
            <TrendingUp className="w-5 h-5 text-teal-600" />
            Behavioral Stability
          </h3>
          <p className="text-sm text-gray-500 mt-1">Consistency and trend analysis</p>
        </div>
        <div className="p-2 bg-slate-50 rounded-full">
          {getStabilityIcon()}
        </div>
      </div>

      <div className="space-y-4 flex-grow">
        <div className="flex items-center justify-between">
          <span className="text-sm text-gray-600 font-medium">Stability Status</span>
          <span className={`text-xs font-bold uppercase tracking-wider ${getStabilityColor()}`}>
            {analysis.stabilityLevel || 'Unknown'}
          </span>
        </div>

        <div>
          <div className="flex justify-between items-end mb-1">
            <p className="text-sm text-gray-600 font-medium">Spending Consistency</p>
            <span className="text-xs font-bold text-slate-400">{Math.round(analysis.consistencyScore * 100)}%</span>
          </div>
          <div className="w-full bg-gray-100 rounded-full h-1.5">
            <div
              className="bg-teal-500 h-1.5 rounded-full transition-all"
              style={{ width: `${analysis.consistencyScore * 100}%` }}
            />
          </div>
          <p className="text-[10px] text-gray-400 mt-1 uppercase tracking-widest font-bold">
            Pattern: {analysis.spendingConsistency || 'Unknown'}
          </p>
        </div>

        {analysis.showsRecoverySignals && (
          <div className="flex items-center gap-2 px-3 py-2 bg-emerald-50 rounded-lg border border-emerald-100">
            <ShieldCheck className="w-3.5 h-3.5 text-emerald-600" />
            <span className="text-[11px] font-bold text-emerald-700 uppercase tracking-tight">Recovery Signals Detected</span>
          </div>
        )}

        <div className="mt-auto pt-4 p-3 bg-slate-50 rounded border border-slate-100">
          <p className="text-sm text-gray-700 italic leading-snug">
            {analysis.stabilityObservation || '—'}
          </p>
        </div>
      </div>
    </div>
  );
};

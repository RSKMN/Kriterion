import React from 'react';
import { SpendingRhythmAnalysis } from '@/services/financial-reflection.service';
import { Clock, Moon, Sun, Sunrise, Sunset } from 'lucide-react';

interface SpendingRhythmReflectionCardProps {
  analysis: SpendingRhythmAnalysis;
}

export const SpendingRhythmReflectionCard: React.FC<SpendingRhythmReflectionCardProps> = ({ analysis }) => {
  const getTimeIcon = () => {
    switch (analysis.timeOfDayPreference) {
      case 'early': return <Sunrise className="w-5 h-5 text-amber-500" />;
      case 'mid': return <Sun className="w-5 h-5 text-orange-500" />;
      case 'late': return <Sunset className="w-5 h-5 text-rose-500" />;
      case 'night': return <Moon className="w-5 h-5 text-indigo-500" />;
      default: return <Clock className="w-5 h-5 text-slate-400" />;
    }
  };

  const getRhythmLabel = () => {
    switch (analysis.rhythmPattern) {
      case 'structured': return 'Highly Structured';
      case 'stable': return 'Stable Rhythms';
      case 'variable': return 'Variable Rhythms';
      case 'bursty': return 'Burst-Oriented';
      case 'irregular': return 'Irregular Rhythms';
      default: return 'Unknown Rhythm';
    }
  };

  return (
    <div className="bg-white rounded-lg border border-gray-200 p-6 h-full flex flex-col">
      <div className="flex items-start justify-between mb-4">
        <div>
          <h3 className="text-lg font-semibold text-gray-900 flex items-center gap-2">
            <Clock className="w-5 h-5 text-blue-600" />
            Spending Rhythm
          </h3>
          <p className="text-sm text-gray-500 mt-1">Temporal patterns and timing</p>
        </div>
        <div className="flex items-center gap-1 px-2 py-1 bg-slate-50 rounded border border-slate-100">
          {getTimeIcon()}
          <span className="text-xs font-bold text-gray-700 uppercase tracking-tight">
            {analysis.timeOfDayPreference}
          </span>
        </div>
      </div>

      <div className="space-y-4 flex-grow">
        <div>
          <div className="flex justify-between items-end mb-1">
            <p className="text-sm text-gray-600 font-medium">Rhythm Pattern</p>
            <span className="text-xs font-bold text-blue-600 uppercase">{getRhythmLabel()}</span>
          </div>
          <div className="w-full bg-gray-100 rounded-full h-2">
            <div
              className="bg-blue-600 h-2 rounded-full transition-all"
              style={{ width: `${(1 - analysis.irregularityScore) * 100}%` }}
            />
          </div>
          <p className="text-[10px] text-gray-400 mt-1 uppercase tracking-widest font-bold">
            Regularity Index: {Math.round((1 - analysis.irregularityScore) * 100)}%
          </p>
        </div>

        {analysis.hasNightSpendingPattern && (
          <div className="flex items-center gap-3 p-3 bg-indigo-50 rounded-lg border border-indigo-100">
            <Moon className="w-4 h-4 text-indigo-600" />
            <div>
              <p className="text-xs font-bold text-indigo-900 uppercase tracking-tight">Active Night Rhythm</p>
              <p className="text-[11px] text-indigo-700">{analysis.lateNightTransactionRate}% of transactions occur after 11 PM</p>
            </div>
          </div>
        )}

        <div className="mt-auto pt-4 p-3 bg-slate-50 rounded border border-slate-100">
          <p className="text-sm text-gray-700 italic leading-snug">
            {analysis.temporalObservation || '—'}
          </p>
        </div>
      </div>
    </div>
  );
};

import React from 'react';
import { FinancialStructureAnalysis } from '@/services/financial-reflection.service';
import { PieChart, CreditCard } from 'lucide-react';

interface FinancialStructureCardProps {
  analysis: FinancialStructureAnalysis;
}

export const FinancialStructureCard: React.FC<FinancialStructureCardProps> = ({ analysis }) => {
  const getStabilityIcon = () => {
    switch (analysis.structureStability) {
      case 'stable':
        return '✓';
      case 'shifting':
        return '↔';
      case 'volatile':
        return '✕';
      default:
        return '–';
    }
  };

  const getStabilityColor = () => {
    switch (analysis.structureStability) {
      case 'stable':
        return 'text-emerald-600';
      case 'shifting':
        return 'text-amber-600';
      case 'volatile':
        return 'text-red-600';
      default:
        return 'text-gray-400';
    }
  };

  return (
    <div className="bg-white rounded-lg border border-gray-200 p-6 h-full flex flex-col">
      <div className="flex items-start justify-between mb-4">
        <div>
          <h3 className="text-lg font-semibold text-gray-900 flex items-center gap-2">
            <PieChart className="w-5 h-5 text-purple-600" />
            Financial Structure
          </h3>
          <p className="text-sm text-gray-500 mt-1">Fixed vs fluid spending balance</p>
        </div>
        <span className={`text-2xl font-bold ${getStabilityColor()}`}>
          {getStabilityIcon()}
        </span>
      </div>

      <div className="space-y-4 flex-grow">
        <div className="grid grid-cols-2 gap-4">
          <div className="p-3 bg-slate-50 rounded-lg">
            <p className="text-xs text-gray-500 font-bold uppercase tracking-wider mb-1">Fixed</p>
            <p className="text-xl font-bold text-gray-900">{Math.round(analysis.recurringRatio * 100)}%</p>
            <div className="w-full bg-gray-200 rounded-full h-1.5 mt-2">
              <div
                className="bg-purple-500 h-1.5 rounded-full transition-all"
                style={{ width: `${analysis.recurringRatio * 100}%` }}
              />
            </div>
          </div>

          <div className="p-3 bg-slate-50 rounded-lg">
            <p className="text-xs text-gray-500 font-bold uppercase tracking-wider mb-1">Fluid</p>
            <p className="text-xl font-bold text-gray-900">{Math.round(analysis.discretionaryRatio * 100)}%</p>
            <div className="w-full bg-gray-200 rounded-full h-1.5 mt-2">
              <div
                className="bg-blue-400 h-1.5 rounded-full transition-all"
                style={{ width: `${analysis.discretionaryRatio * 100}%` }}
              />
            </div>
          </div>
        </div>

        {/* Subscription Pressure Section */}
        {analysis.subscriptionPressure && (
          <div className="pt-2 border-t border-slate-100">
            <div className="flex items-center gap-2 mb-2">
              <CreditCard className="w-4 h-4 text-slate-400" />
              <span className="text-sm font-semibold text-gray-700">Subscription Pressure</span>
            </div>
            <div className="flex items-center justify-between mb-1">
              <span className="text-xs text-gray-500">{analysis.subscriptionPressure.activeSubscriptionCount} active services</span>
              <span className={`text-[10px] font-bold uppercase px-1.5 py-0.5 rounded ${
                analysis.subscriptionPressure.pressureLevel === 'high' ? 'bg-red-50 text-red-700' :
                analysis.subscriptionPressure.pressureLevel === 'moderate' ? 'bg-amber-50 text-amber-700' :
                'bg-emerald-50 text-emerald-700'
              }`}>
                {analysis.subscriptionPressure.pressureLevel} pressure
              </span>
            </div>
            <div className="w-full bg-gray-100 rounded-full h-1">
              <div
                className={`h-1 rounded-full transition-all ${
                  analysis.subscriptionPressure.pressureScore > 0.6 ? 'bg-red-400' : 'bg-slate-400'
                }`}
                style={{ width: `${analysis.subscriptionPressure.pressureScore * 100}%` }}
              />
            </div>
          </div>
        )}

        <div className="mt-auto pt-4 p-3 bg-slate-50 rounded border border-slate-100">
          <p className="text-sm text-gray-700 italic leading-snug">
            {analysis.structuralObservation || analysis.subscriptionPressure?.pressureObservation || '—'}
          </p>
        </div>
      </div>
    </div>
  );
};

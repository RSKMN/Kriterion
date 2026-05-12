import React from 'react';
import { ReflectionResponse } from '@/services/financial-reflection.service';
import { Lightbulb } from 'lucide-react';

interface ReflectionInsightCardsProps {
  reflections: ReflectionResponse[];
}

export const ReflectionInsightCards: React.FC<ReflectionInsightCardsProps> = ({ reflections }) => {
  const significantReflections = reflections.filter(r => r.isSignificant);

  if (significantReflections.length === 0) return null;

  return (
    <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
      {significantReflections.slice(0, 4).map((reflection, idx) => (
        <div key={idx} className="bg-white rounded-lg border border-slate-200 p-5 hover:border-blue-200 transition-colors">
          <div className="flex items-center gap-2 mb-3">
            <div className="p-1.5 bg-blue-50 rounded-md">
              <Lightbulb className="w-4 h-4 text-blue-600" />
            </div>
            <span className="text-xs font-bold text-slate-400 uppercase tracking-widest">
              Critical Insight
            </span>
          </div>
          <h4 className="font-semibold text-slate-900 mb-2">{reflection.title}</h4>
          <p className="text-sm text-slate-600 leading-relaxed">
            {reflection.observation}
          </p>
        </div>
      ))}
    </div>
  );
};

import React, { useEffect, useState } from 'react';
import { financialReflectionService, FinancialReflection } from '@/services/financial-reflection.service';
import { SpendingRhythmReflectionCard } from './components/SpendingRhythmReflectionCard';
import { TransactionDensityCard } from './components/TransactionDensityCard';
import { FinancialStructureCard } from './components/FinancialStructureCard';
import { BehavioralStabilityCard } from './components/BehavioralStabilityCard';
import { ReflectionJournalCard } from './components/ReflectionJournalCard';
import { ReflectionTimeline } from './components/ReflectionTimeline';
import { ReflectionInsightCards } from './components/ReflectionInsightCards';
import { AlertCircle, RefreshCw, Loader, Info, Database } from 'lucide-react';
import { apiClient } from '@/services/api';

export const FinancialReflectionPage: React.FC = () => {
  const [reflection, setReflection] = useState<FinancialReflection | null>(null);
  const [loading, setLoading] = useState(true);
  const [seeding, setSeeding] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const loadReflection = async () => {
    try {
      setLoading(true);
      setError(null);
      const data = await financialReflectionService.getFinancialReflection();
      setReflection(data);
    } catch (err) {
      console.error('Error loading reflection:', err);
      setError('Unable to load financial reflection analysis. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  const handleSeed = async () => {
    try {
      setSeeding(true);
      await apiClient.post('/v1/financial-reflection/seed');
      await loadReflection();
    } catch (err) {
      console.error('Error seeding data:', err);
    } finally {
      setSeeding(false);
    }
  };

  useEffect(() => {
    loadReflection();
  }, []);

  if (loading) {
    return (
      <div className="min-h-screen bg-[#f8fafc] p-8 flex items-center justify-center">
        <div className="text-center">
          <Loader className="w-8 h-8 text-slate-400 animate-spin mx-auto mb-4" />
          <p className="text-slate-500 font-medium tracking-tight">Analyzing behavioral rhythms...</p>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="min-h-screen bg-[#f8fafc] p-8">
        <div className="max-w-4xl mx-auto">
          <div className="bg-white rounded-xl border border-red-100 p-8 text-center">
            <AlertCircle className="w-10 h-10 text-red-400 mx-auto mb-4" />
            <h2 className="text-xl font-semibold text-slate-900 mb-2">Analysis Interrupted</h2>
            <p className="text-slate-500 mb-6">{error}</p>
            <button
              onClick={loadReflection}
              className="px-6 py-2.5 bg-slate-900 text-white rounded-lg hover:bg-slate-800 transition-all font-medium flex items-center gap-2 mx-auto"
            >
              <RefreshCw className="w-4 h-4" />
              Retry Analysis
            </button>
          </div>
        </div>
      </div>
    );
  }

  if (!reflection) return null;

  const isInsufficientData = reflection.dataStatus === 'insufficient_data';

  return (
    <div className="min-h-screen bg-[#f8fafc] p-6 lg:p-10">
      <div className="max-w-7xl mx-auto">
        {/* Header Section */}
        <header className="mb-10 flex flex-col md:flex-row md:items-end justify-between gap-6">
          <div className="max-w-2xl">
            <h1 className="text-3xl font-bold text-slate-900 tracking-tight mb-2">
              Financial Cognitive Reflection
            </h1>
            <p className="text-slate-500 text-lg leading-relaxed">
              An analytical mirror of your behavioral transaction patterns and spending rhythms.
            </p>
          </div>
          <div className="flex items-center gap-3">
            {isInsufficientData && (
              <button
                onClick={handleSeed}
                disabled={seeding}
                className="px-4 py-2 bg-white border border-slate-200 text-slate-600 rounded-lg hover:bg-slate-50 transition-all text-sm font-semibold flex items-center gap-2"
              >
                {seeding ? <Loader className="w-4 h-4 animate-spin" /> : <Database className="w-4 h-4" />}
                Seed Demo Rhythms
              </button>
            )}
            <button
              onClick={loadReflection}
              className="p-2.5 bg-white border border-slate-200 text-slate-600 rounded-lg hover:border-slate-300 transition-all"
              title="Refresh analysis"
            >
              <RefreshCw className="w-5 h-5" />
            </button>
          </div>
        </header>

        {isInsufficientData ? (
          <div className="bg-white rounded-2xl border border-slate-200 p-12 text-center max-w-3xl mx-auto">
            <div className="w-16 h-16 bg-slate-50 rounded-full flex items-center justify-center mx-auto mb-6">
              <Info className="w-8 h-8 text-slate-300" />
            </div>
            <h2 className="text-2xl font-bold text-slate-900 mb-3">Insufficient Temporal Data</h2>
            <p className="text-slate-500 mb-8 leading-relaxed">
              Financial reflections improve as more transaction rhythms become available. 
              We need at least a few weeks of activity to generate meaningful behavioral observations.
            </p>
            <div className="grid grid-cols-1 md:grid-cols-3 gap-4 text-left">
              <div className="p-4 bg-slate-50 rounded-xl border border-slate-100">
                <p className="text-xs font-bold text-slate-400 uppercase tracking-widest mb-1">Status</p>
                <p className="text-sm font-semibold text-slate-700">Waiting for patterns</p>
              </div>
              <div className="p-4 bg-slate-50 rounded-xl border border-slate-100">
                <p className="text-xs font-bold text-slate-400 uppercase tracking-widest mb-1">Required</p>
                <p className="text-sm font-semibold text-slate-700">5+ Transactions</p>
              </div>
              <div className="p-4 bg-slate-50 rounded-xl border border-slate-100">
                <p className="text-xs font-bold text-slate-400 uppercase tracking-widest mb-1">Mode</p>
                <p className="text-sm font-semibold text-slate-700">Observation Only</p>
              </div>
            </div>
          </div>
        ) : (
          <div className="space-y-10">
            {/* Primary Analysis Grid */}
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
              <SpendingRhythmReflectionCard analysis={reflection.spendingRhythm} />
              <TransactionDensityCard analysis={reflection.transactionDensity} />
              <FinancialStructureCard analysis={reflection.financialStructure} />
              <BehavioralStabilityCard analysis={reflection.behavioralStability} />
            </div>

            {/* Critical Insights Section */}
            <section>
              <h2 className="text-xl font-bold text-slate-900 mb-6 flex items-center gap-2">
                Significant Behavioral Patterns
              </h2>
              <ReflectionInsightCards reflections={reflection.reflections} />
            </section>

            {/* Detailed Reflection & Timeline */}
            <div className="grid grid-cols-1 lg:grid-cols-3 gap-10">
              <div className="lg:col-span-2">
                <ReflectionJournalCard reflections={reflection.reflections} />
              </div>
              <div>
                <ReflectionTimeline reflections={reflection.reflections} />
              </div>
            </div>

            {/* Footer Status */}
            <footer className="pt-10 border-t border-slate-100 flex flex-wrap items-center justify-between gap-4 text-xs font-medium text-slate-400 uppercase tracking-widest">
              <div className="flex items-center gap-6">
                <span>Confidence: {Math.round(reflection.analysisConfidence * 100)}%</span>
                <span>Data: {reflection.dataStatus}</span>
                <span>Stability: {reflection.overallStability}</span>
              </div>
              <div>
                Generated: {new Date(reflection.generatedAt).toLocaleString()}
              </div>
            </footer>
          </div>
        )}
      </div>
    </div>
  );
};

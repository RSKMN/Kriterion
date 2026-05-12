import React from 'react';
import { BehavioralDashboard } from '@/features/behavioral-analytics/components/BehavioralDashboard';
import { PredictiveDashboard } from '@/features/behavioral-analytics/components/PredictiveDashboard';

const BehavioralAnalyticsPage: React.FC = () => {
    return (
        <div className="container mx-auto px-4 py-8 space-y-10">
            <BehavioralDashboard />
            <PredictiveDashboard />
        </div>
    );
};

export default BehavioralAnalyticsPage;
